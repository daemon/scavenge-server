package org.scavenge.economy.base;

import org.scavenge.ranks.Rank;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SCUniqueAccount implements UniqueAccount {
  private final UUID uuid;
  private final UserDatabase database;
  private final String displayName;
  private final Rank rank;
  private double balance;

  public SCUniqueAccount(UserDatabase database, String uuid, String displayName, double balance, int rank) {
    this.uuid = UUID.fromString(uuid);
    this.database = database;
    this.displayName = displayName;
    this.balance = balance;
    this.rank = Rank.values()[rank];
  }

  public Rank rank() {
    return this.rank;
  }

  @Override
  public Text getDisplayName() {
    return Text.of(this.displayName);
  }

  @Override
  public BigDecimal getDefaultBalance(Currency currency) {
    return BigDecimal.ZERO;
  }

  @Override
  public boolean hasBalance(Currency currency, Set<Context> contexts) {
    return currency instanceof SCDollar;
  }

  @Override
  public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
    return BigDecimal.valueOf(this.balance);
  }

  @Override
  public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
    Map<Currency, BigDecimal> data = new HashMap<>();
    data.put(new SCDollar(), BigDecimal.valueOf(this.balance));
    return data;
  }

  @Override
  public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
    if (amount.doubleValue() >= this.balance)
      return this.deposit(currency, amount.subtract(BigDecimal.valueOf(this.balance)), cause, contexts);
    else
      return this.withdraw(currency, BigDecimal.valueOf(this.balance).subtract(amount), cause, contexts);
  }

  @Override
  public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
    return null;
  }

  @Override
  public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
    return this.withdraw(currency, BigDecimal.valueOf(this.balance), cause, contexts);
  }

  @Override
  public synchronized TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
    ResultType result = this.database.setBalance(this.uuid.toString(), this.balance + amount.doubleValue());
    if (result == ResultType.SUCCESS)
      this.balance += amount.doubleValue();
    return new SCTransactionResult(this, amount, contexts, result, TransactionTypes.DEPOSIT);
  }

  @Override
  public synchronized TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
    ResultType result = this.database.setBalance(this.uuid.toString(), this.balance - amount.doubleValue());
    if (result == ResultType.SUCCESS)
      this.balance -= amount.doubleValue();
    return new SCTransactionResult(this, amount, contexts, result, TransactionTypes.WITHDRAW);
  }

  @Override
  public synchronized TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
    ResultType result = this.database.transfer(this.uuid.toString(), to.getIdentifier(), amount.doubleValue());
    SCUniqueAccount otherAcc = null;
    if (to instanceof SCUniqueAccount)
      otherAcc = (SCUniqueAccount) to;
    if (result == ResultType.SUCCESS) {
      this.balance -= amount.doubleValue();
      if (otherAcc != null)
        otherAcc.balance += amount.doubleValue();
    }
    return new SCTransferResult(this, to, amount, contexts, result, TransactionTypes.TRANSFER);
  }

  @Override
  public String getIdentifier() {
    return this.uuid.toString();
  }

  @Override
  public Set<Context> getActiveContexts() {
    return null;
  }

  @Override
  public UUID getUniqueId() {
    return this.uuid;
  }
}
