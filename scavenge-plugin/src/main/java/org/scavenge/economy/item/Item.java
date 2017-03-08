package org.scavenge.economy.item;

import org.scavenge.economy.item.ItemUtils;
import org.spongepowered.api.item.inventory.ItemStack;

public class Item {
  private final int id;
  private final int dv;
  private final String name;
  private final String idName;
  private final int maxStack;

  public Item(int id, String idName, int dv, String name, int maxStack) {
    this.id = id;
    this.idName = idName;
    this.dv = dv;
    this.name = name;
    this.maxStack = maxStack;
  }

  public ItemStack toItemStack(int quantity) {
    return ItemUtils.createItemStack(this.idName, this.dv, quantity);
  }

  public ItemStack[] toItemStacks(int quantity) {
    int nStacks = (int) Math.ceil(quantity / ((double) this.maxStack));
    ItemStack[] stacks = new ItemStack[nStacks];
    for (int i = 0; i < nStacks - 1; ++i)
      stacks[i] = ItemUtils.createItemStack(this.idName, this.dv, this.maxStack);
    stacks[nStacks - 1] = ItemUtils.createItemStack(this.idName, this.dv, quantity - this.maxStack * (nStacks - 1));
    return stacks;
  }

  public String displayName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Item))
      return false;
    return ((Item) other).id == this.id;
  }
}
