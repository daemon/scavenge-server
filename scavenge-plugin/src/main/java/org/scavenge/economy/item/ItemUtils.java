package org.scavenge.economy.item;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.item.inventory.ItemStack;

public class ItemUtils {
  public static ItemStack createItemStack(String id) {
    return createItemStack(id, 0, 1);
  }

  public static ItemStack createItemStack(String id, int damage) {
    return createItemStack(id, damage, 1);
  }

  public static ItemStack createItemStack(String id, int damage, int count) {
    DataContainer container = new MemoryDataContainer();
    container.set(DataQuery.of("UnsafeDamage"), damage);
    container.set(DataQuery.of("ItemType"), id);
    container.set(DataQuery.of("ContentVersion"), 1);
    container.set(DataQuery.of("Count"), count);
    return ItemStack.builder().fromContainer(container).build();
  }

  public static int damageValue(ItemStack item) {
    return (Integer) item.toContainer().get(DataQuery.of("UnsafeDamage")).get();
  }

  public static String displayName(ItemStack item) {
    return item.getTranslation().get();
  }

  public static String id(ItemStack item) {
    return item.getItem().getId();
  }
}
