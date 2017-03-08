package org.scavenge.economy.shop.data;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class ChestData extends AbstractSingleData<ShopChest, ChestData, ImmutableChestData> {
  public static final Key<Value<ShopChest>> KEY = KeyFactory.makeSingleKey(TypeToken.of(ShopChest.class),
      new TypeToken<Value<ShopChest>>(){}, DataQuery.of("shop"), "scavenge:shop", "ChestShop");
  protected ChestData(ShopChest value) {
    super(value, KEY);
  }

  @Override
  protected Value<?> getValueGetter() {
    return Sponge.getRegistry().getValueFactory().createValue(KEY, this.getValue());
  }

  @Override
  public Optional<ChestData> fill(DataHolder dataHolder, MergeFunction overlap) {
    ChestData chestData = Preconditions.checkNotNull(overlap).merge(copy(), dataHolder.get(ChestData.class).orElse(copy()));
    return Optional.of(set(KEY, chestData.get(KEY).get()));
  }

  @Override
  public Optional<ChestData> from(DataContainer container) {
    if (container.contains(KEY.getQuery()))
      return Optional.of(set(KEY, container.getSerializable(KEY.getQuery(), ShopChest.class).orElse(getValue())));
    return Optional.empty();
  }

  @Override
  public ChestData copy() {
    return new ChestData(this.getValue());
  }

  @Override
  public ImmutableChestData asImmutable() {
    return new ImmutableChestData(this.getValue());
  }

  @Override
  public int getContentVersion() {
    return 2;
  }
}
