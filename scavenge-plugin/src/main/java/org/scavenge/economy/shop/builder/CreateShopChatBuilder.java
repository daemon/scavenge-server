package org.scavenge.economy.shop.builder;

import org.apache.commons.lang3.StringUtils;
import org.scavenge.ScavengePlugin;
import org.scavenge.build.GPUtils;
import org.scavenge.economy.item.Item;
import org.scavenge.economy.item.ItemDatabase;
import org.scavenge.economy.shop.ShopDatabase;
import org.scavenge.economy.shop.data.ChestData;
import org.scavenge.economy.shop.data.ShopChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreateShopChatBuilder extends ShopChatBuilder {
  private final ItemDatabase itemDb;
  private final ShopDatabase shopDb;
  private Item item;
  private int quantity = 0;
  private Location chestLocation;

  enum Step {CLICK_CHEST, BUY_OR_SELL, SELL_HAND_ITEM_INPUT, BUY_ITEM_SEARCH, ITEM_PRICE, BUY_ITEM_QUANTITY}
  private Step step = Step.CLICK_CHEST;
  private boolean buying;
  private double price = 0.0;

  public CreateShopChatBuilder(Player player, ItemDatabase itemDb, ShopDatabase shopDb) throws IllegalStateException {
    super(player);
    this.prompt();
    this.itemDb = itemDb;
    this.shopDb = shopDb;
  }

  private void prompt() {
    if (this.step == Step.CLICK_CHEST)
      this.player().sendMessage(Text.of(TextColors.GOLD, "Please begin by clicking the wooden chest you wish to turn into a shop."));
    else if (this.step == Step.BUY_OR_SELL)
      this.player().sendMessage(Text.of(TextColors.GOLD, "Are you ", TextColors.AQUA, "selling", TextColors.GOLD, " or ",
          TextColors.AQUA, "buying", TextColors.GOLD, "?"));
    else if (this.step == Step.SELL_HAND_ITEM_INPUT)
      this.player().sendMessage(Text.of(TextColors.GOLD, "Please hold the item you wish to sell in your hand. Send \"OK\" when you have the item held."));
    else if (this.step == Step.BUY_ITEM_SEARCH)
      this.player().sendMessage(Text.of(TextColors.GOLD, "Please tell me the name of the item you wish to buy (e.g. bacon): "));
    else if (this.step == Step.ITEM_PRICE) {
      if (this.buying) {
        this.player().sendMessage(Text.of(TextColors.GOLD, "How much will you pay for one single item? (e.g. 3.99)"));
      } else {
        this.player().sendMessage(Text.of(TextColors.GOLD, "How much will you sell one single item for? (e.g. 3.99)"));
      }
    } else {
      this.player().sendMessage(Text.of(TextColors.GOLD, "How many items are you willing to buy at " + price + " each?"));
    }
  }

  private void complete() {
    this.finish();
    if (!this.chestLocation.getBlock().getType().equals(BlockTypes.CHEST)) {
      this.player().sendMessage(Text.of(TextColors.RED, "The block you've selected is no longer a chest! Shop creation aborted."));
      return;
    }
    this.player().sendMessage(Text.of(TextColors.GREEN, "Success! The chest you selected is now a shop."));
    if (!this.buying)
      this.player().sendMessage(Text.of(TextColors.GOLD, "To begin selling, stock the chest with the item."));
    /* ChestData data = Sponge.getDataManager().getManipulatorBuilder(ChestData.class).get().create();
    data.set(ChestData.KEY, new ShopChest());*/
    // TODO fucking db shit
  }

  @Override
  public void receiveClickBlockInput(Location<World> location) {
    if (this.step == Step.CLICK_CHEST) {
      if (!location.getBlock().getType().equals(BlockTypes.CHEST)) {
        this.player().sendMessage(Text.of(TextColors.RED, "Block must be a wooden chest."));
        return;
      }
      if (!GPUtils.canBuild(this.player(), location)) {
        this.player().sendMessage(Text.of(TextColors.RED, "You're not allowed to build here!"));
        return;
      }
      this.chestLocation = location;
      this.step = Step.BUY_OR_SELL;
      this.prompt();
    }
  }

  @Override
  public void receiveTextInput(String text) {
    text = text.trim().toLowerCase();
    if (step == Step.BUY_OR_SELL) {
      if (text.equals("selling") || text.equals("sell")) {
        this.buying = false;
        this.step = Step.SELL_HAND_ITEM_INPUT;
        this.prompt();
      } else if (text.equals("buying") || text.equals("buy")) {
        this.buying = true;
        this.step = Step.BUY_ITEM_SEARCH;
        this.prompt();
      } else {
        this.player().sendMessage(Text.of(TextColors.RED, "You must say \"buying\" or \"selling\"!"));
        this.prompt();
        return;
      }
    } else if (step == Step.SELL_HAND_ITEM_INPUT) {
      if (!Arrays.stream(new String[] {"ok", "yes", "got it", "okay", "yee", "k", "kk"}).anyMatch(text::equals)) {
        this.player().sendMessage(Text.of(TextColors.RED, "You must send \"OK\"!"));
        return;
      }
      Optional<ItemStack> item = this.player().getItemInHand(HandTypes.MAIN_HAND);
      if (!item.isPresent()) {
        this.player().sendMessage(Text.of(TextColors.RED, "You must be holding an item you wish to sell!"));
        return;
      }
      Item dbItem;
      try {
        dbItem = this.itemDb.findItem(item.get());
      } catch (SQLException e) {
        this.player().sendMessage(Text.of(TextColors.RED, "Unknown database error."));
        return;
      }
      if (dbItem == null) {
        this.player().sendMessage(Text.of(TextColors.RED, "Sorry, that item cannot be sold. Please try a different item."));
        return;
      }
      this.item = dbItem;
      this.step = Step.ITEM_PRICE;
      this.prompt();
    } else if (this.step == Step.ITEM_PRICE) {
      try {
        this.price = Double.parseDouble(text.replace("$", ""));
        if (this.buying) {
          this.step = Step.BUY_ITEM_QUANTITY;
          this.prompt();
        } else
          this.complete();
      } catch (Exception e) {
        e.printStackTrace();
        this.player().sendMessage(Text.of(TextColors.RED, "Please enter a decimal number like 3.99."));
      }
    } else if (this.step == Step.BUY_ITEM_QUANTITY) {
      try {
        this.quantity = Integer.parseInt(text);
        if (this.quantity <= 0)
          throw new IllegalArgumentException("Needs to be positive number");
        this.complete();
      } catch (Exception e) {
        this.player().sendMessage(Text.of(TextColors.RED, "Please enter a positive number like 3."));
      }
    } else if (this.step == Step.BUY_ITEM_SEARCH) {
      Item item = null;
      try {
        item = this.itemDb.findItem(text);
      } catch (SQLException e) {
        this.player().sendMessage(Text.of(TextColors.RED, "Database failure."));
        return;
      }
      if (item != null) {
        this.item = item;
        this.step = Step.BUY_ITEM_QUANTITY;
        this.prompt();
        return;
      }
      if (!StringUtils.isAsciiPrintable(text) || text.length() < 1) {
        this.player().sendMessage(Text.of(TextColors.RED, "You must type in at least 1 character to trigger search!"));
        return;
      }
      try {
        List<String> items = this.itemDb.findItemsByDisplayPrefix(text);
        if (items.size() == 1) {
          this.item = this.itemDb.findItem(items.get(0));
          this.player().sendMessage(Text.of(TextColors.GOLD, "Matched item ", TextColors.AQUA, this.item.displayName()));
          this.step = Step.ITEM_PRICE;
          this.prompt();
        } else if (items.size() == 0) {
          this.player().sendMessage(Text.of(TextColors.RED, "Couldn't find any items with that query!"));
        } else {
          this.player().sendMessage(Text.of(TextColors.GOLD, "The following tradeable items matched your query:"));
          for (String itemName : items)
            this.player().sendMessage(Text.of(TextColors.AQUA, itemName));
          this.player().sendMessage(Text.of(TextColors.GOLD, "Please type out the full name of one of the above items to choose it."));
        }
      } catch (SQLException e) {
        this.player().sendMessage(Text.of(TextColors.RED, "Database failure."));
      }
    }
  }
}
