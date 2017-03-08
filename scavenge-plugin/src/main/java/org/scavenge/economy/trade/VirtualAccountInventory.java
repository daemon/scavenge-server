package org.scavenge.economy.trade;

import org.scavenge.gui.HookedInventory;
import org.scavenge.web.NoAccountException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualAccountInventory {
  private final HookedInventory inventory;
  private final Player player;
  private final TradeDatabase tradeDb;
  private static final String TITLE = "Online trading account";
  private static final Map<UUID, AtomicInteger> blockedUuids = new HashMap<>();

  public VirtualAccountInventory(TradeDatabase tradeDatabase, Player player) {
    this.inventory = new HookedInventory(player, TITLE);
    this.inventory.onInventoryChange(new ChangeListener());
    this.player = player;
    this.tradeDb = tradeDatabase;
  }

  public synchronized static void blockUuid(UUID player) {
    if (blockedUuids.containsKey(player))
      blockedUuids.get(player).incrementAndGet();
    else
      blockedUuids.put(player, new AtomicInteger(1));
    Optional<Player> p = Sponge.getServer().getPlayer(player);
    if (p.isPresent() && HookedInventory.activeInventories().containsKey(p.get()) &&
        HookedInventory.activeInventories().get(p.get()).title().equals(TITLE)) {
      p.get().sendMessage(Text.of(TextColors.GOLD, "Your online inventory has been closed because you're using the website."));
      HookedInventory.activeInventories().get(p.get()).close();
    }
  }

  public synchronized static void unblockUuid(UUID player) {
    if (blockedUuids.containsKey(player)) {
      if (blockedUuids.get(player).get() == 0)
        return;
      blockedUuids.get(player).decrementAndGet();
    }
  }

  public Inventory inventory() {
    return this.inventory.inventory();
  }

  public Player player() {
    return this.player;
  }

  public void open() {
    if (blockedUuids.containsKey(this.player.getUniqueId()) && blockedUuids.get(this.player.getUniqueId()).get() > 0) {
      this.player.sendMessage(Text.of(TextColors.RED, "Your online inventory cannot be opened because you're using the website."));
      return;
    }
    List<ItemStack> stacks = null;
    try {
      stacks = this.tradeDb.fetchInventoryItems(this.player);
    } catch (NoAccountException e) {
      this.player.sendMessage(Text.of(TextColors.RED, "You need to ", TextColors.AQUA, "/register", TextColors.RED, " an account with the website first!"));
      return;
    } catch (SQLException e) {
      this.player.sendMessage(Text.of(TextColors.RED, "Database error."));
      return;
    }
    stacks.forEach(this.inventory()::offer);
    this.inventory.open();
  }

  public void close() {
    this.inventory.close();
  }

  private class ChangeListener implements HookedInventory.InventoryChangeListener {
    @Override
    public boolean onInventoryChange() {
      boolean success = tradeDb.setInventory(player, inventory());
      if (!success)
        player.sendMessage(Text.of(TextColors.RED, "That item isn't tradeable!"));
      return success;
    }
  }
}
