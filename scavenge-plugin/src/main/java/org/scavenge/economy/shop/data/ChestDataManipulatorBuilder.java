package org.scavenge.economy.shop.data;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class ChestDataManipulatorBuilder implements DataManipulatorBuilder<ChestData, ImmutableChestData> {

  @Override
  public ChestData create() {
    return new ChestData(new ShopChest());
  }

  @Override
  public Optional<ChestData> createFrom(DataHolder dataHolder) {
    return this.create().fill(dataHolder);
  }

  @Override
  public Optional<ChestData> build(DataView container) throws InvalidDataException {
    if (!container.contains(ChestData.KEY.getQuery()))
      return Optional.empty();
    ShopChest chest = container.getSerializable(ChestData.KEY.getQuery(), ShopChest.class).get();
    return Optional.of(new ChestData(chest));
  }
}
