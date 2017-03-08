package org.scavenge.economy.shop.data;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;

public class ShopChest implements DataSerializable {
  @Override
  public int getContentVersion() {
    return 1;
  }

  @Override
  public DataContainer toContainer() {
    return new MemoryDataContainer().set(DataQuery.of("shoptest"), "fuck");
  }
}
