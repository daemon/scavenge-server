package org.scavenge.economy.base;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SCDollar implements Currency {
  public static SCDollar instance = new SCDollar();
  @Override
  public Text getDisplayName() {
    return Text.of("dollar");
  }

  @Override
  public Text getPluralDisplayName() {
    return Text.of("dollars");
  }

  @Override
  public Text getSymbol() {
    return Text.of("$");
  }

  @Override
  public Text format(BigDecimal amount, int numFractionDigits) {
    return this.getSymbol().concat(Text.of(Math.round(amount.doubleValue() * 100) / 100.0));
  }

  @Override
  public int getDefaultFractionDigits() {
    return 2;
  }

  @Override
  public boolean isDefault() {
    return true;
  }

  @Override
  public String getId() {
    return "scavengecraftdollar";
  }

  @Override
  public String getName() {
    return "SCDollar";
  }
}
