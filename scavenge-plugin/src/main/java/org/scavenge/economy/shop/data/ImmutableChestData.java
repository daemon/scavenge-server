package org.scavenge.economy.shop.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableChestData extends AbstractImmutableSingleData<ShopChest, ImmutableChestData, ChestData> {
  protected ImmutableChestData(ShopChest value) {
    super(value, ChestData.KEY);
  }

  @Override
  protected ImmutableValue<?> getValueGetter() {
    return Sponge.getRegistry().getValueFactory().createValue(ChestData.KEY, this.getValue()).asImmutable();
  }

  @Override
  public ChestData asMutable() {
    return new ChestData(this.getValue());
  }

  @Override
  public int getContentVersion() {
    return 2;
  }
}
