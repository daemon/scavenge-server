package org.scavenge.economy.shop.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class ShopChestBuilder implements DataBuilder<ShopChest> {
  @Override
  public Optional<ShopChest> build(DataView container) throws InvalidDataException {
    if (!container.contains(DataQuery.of("shoptest")))
      return Optional.empty();
    return Optional.of(new ShopChest());
  }
}
