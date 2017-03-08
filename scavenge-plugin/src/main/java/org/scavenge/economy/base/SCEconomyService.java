package org.scavenge.economy.base;

import com.google.common.collect.Sets;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SCEconomyService implements EconomyService {
  private final UserDatabase database;

  public SCEconomyService(UserDatabase database) {
    this.database = database;
  }

  @Override
  public Currency getDefaultCurrency() {
    return new SCDollar();
  }

  @Override
  public Set<Currency> getCurrencies() {
    return Sets.newHashSet(this.getDefaultCurrency());
  }

  @Override
  public boolean hasAccount(UUID uuid) {
    this.getOrCreateAccount(uuid);
    return true;
  }

  @Override
  public boolean hasAccount(String identifier) {
    return false;
  }

  @Override
  public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
    Optional<Player> player = Sponge.getServer().getPlayer(uuid);
    if (!player.isPresent())
      return Optional.empty();
    String name = player.get().getName();
    try {
      return Optional.ofNullable(this.database.getOrCreateAccount(uuid, name, false));
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  @Override
  public Optional<Account> getOrCreateAccount(String identifier) {
    return Optional.empty();
  }

  @Override
  public void registerContextCalculator(ContextCalculator<Account> calculator) {
  }
}
