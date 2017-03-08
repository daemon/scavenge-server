package org.scavenge.economy.shop;

import org.scavenge.economy.item.Item;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ShopChestBlock {
  private static final Text TITLE = Text.of("Chest shop");
  private final Item item;
  private final boolean buying;
  private final int quantity;
  private final double price;

  public ShopChestBlock(Item item, boolean buying, int quantity, double price) {
    this.item = item;
    this.buying = buying;
    this.quantity = quantity;
    this.price = price;
  }
}
