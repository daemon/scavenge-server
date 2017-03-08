package org.scavenge.economy.base;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

public class SCTransferResult implements TransferResult {
  private final Account fromAccount;
  private final Account toAccount;
  private final BigDecimal amount;
  private final Set<Context> contexts;
  private final ResultType result;
  private final TransactionType type;

  public SCTransferResult(Account from, Account to, BigDecimal amount, Set<Context> contexts, ResultType result, TransactionType type) {
    this.fromAccount = from;
    this.toAccount = to;
    this.amount = amount;
    this.contexts = contexts;
    this.result = result;
    this.type = type;
  }

  @Override
  public Account getAccountTo() {
    return this.toAccount;
  }

  @Override
  public Account getAccount() {
    return this.fromAccount;
  }

  @Override
  public Currency getCurrency() {
    return new SCDollar();
  }

  @Override
  public BigDecimal getAmount() {
    return this.amount;
  }

  @Override
  public Set<Context> getContexts() {
    return this.contexts;
  }

  @Override
  public ResultType getResult() {
    return this.result;
  }

  @Override
  public TransactionType getType() {
    return this.type;
  }
}
