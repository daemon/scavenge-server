package org.scavenge.gui;

import org.scavenge.ScavengePlugin;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

import java.util.*;

public class HookedInventory {
  private static final Map<Player, HookedInventory> activeInventories = new HashMap<>();
  private final Inventory inventory;
  private final Player player;
  private final String title;
  private int pageNo = 0;
  private InventoryChangeListener inventoryChangeListener;

  public HookedInventory(Player player, String title) {
    this.title = title;
    this.inventory = Inventory.builder().property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(title)))
        .of(InventoryArchetypes.DOUBLE_CHEST).build(ScavengePlugin.instance);
    this.player = player;
  }

  public static Map<Player, HookedInventory> activeInventories() {
    return activeInventories;
  }

  public String title() {
    return this.title;
  }

  public void open() {
    activeInventories.put(this.player, this);
    this.player.openInventory(this.inventory, Cause.source(ScavengePlugin.instance).build());
  }

  public void close() {
    if (activeInventories.containsKey(this.player))
      this.player.closeInventory(Cause.source(ScavengePlugin.instance).build());
    activeInventories.remove(this.player);
  }

  public HookedInventory onInventoryChange(InventoryChangeListener listener) {
    this.inventoryChangeListener = listener;
    return this;
  }

  public Inventory inventory() {
    return this.inventory;
  }

  @FunctionalInterface
  public interface InventoryChangeListener {
    boolean onInventoryChange();
  }

  public static class Listener {
    @org.spongepowered.api.event.Listener
    public void onInventoryClose(InteractInventoryEvent.Close event) {
      Player player = event.getCause().first(Player.class).get();
      activeInventories.remove(player);
    }

    @org.spongepowered.api.event.Listener
    public void onInventoryChange(ChangeInventoryEvent event) {
      if (event.isCancelled())
        return;
      Optional<Player> player = event.getCause().first(Player.class);
      if (!player.isPresent())
        return;
      HookedInventory hookedInventory = activeInventories.get(player.get());
      if (hookedInventory == null)
        return;
      if (!event.getTargetInventory().getName().get().equals(hookedInventory.title))
        return;
      if (hookedInventory.inventoryChangeListener != null)
        event.setCancelled(!hookedInventory.inventoryChangeListener.onInventoryChange());
    }
  }
}
