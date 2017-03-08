package org.scavenge.economy.base;

import org.scavenge.ScavengePlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class PayCommand implements CommandExecutor {
  private final SCEconomyService service;

  public PayCommand(SCEconomyService service) {
    this.service = service;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    UniqueAccount account = this.service.getOrCreateAccount(player.getUniqueId()).get();
    Player payee = (Player) args.getOne("player").get();
    double amount = (double) args.getOne("amount").get();
    if (amount < 0) {
      player.sendMessage(Text.of(TextColors.RED, "Amount must be positive!"));
      return CommandResult.empty();
    }
    UniqueAccount payeeAcc = this.service.getOrCreateAccount(payee.getUniqueId()).get();
    TransferResult result = account.transfer(payeeAcc, SCDollar.instance, BigDecimal.valueOf(amount),
        Cause.source(ScavengePlugin.instance).build());
    if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
      player.sendMessage(Text.of(TextColors.RED, "You don't have enough money!"));
    else if (result.getResult() == ResultType.FAILED)
      player.sendMessage(Text.of(TextColors.RED, "Unknown database error!"));
    else if (result.getResult() == ResultType.SUCCESS) {
      player.sendMessage(Text.of(TextColors.GREEN, "You sent $" + (Math.round(amount * 100) / 100.0) + " to " + payee.getName()));
      payee.sendMessage(Text.of(TextColors.GREEN, player.getName(), " gave you $", Math.round(amount * 100) / 100.0, "."));
    }
    return CommandResult.success();
  }

  public static CommandSpec createSpec(SCEconomyService service) {
    return CommandSpec.builder().description(Text.of("Pays other player the specified amount."))
        .executor(new PayCommand(service))
        .permission("scavenge.cmd.pay")
        .arguments(GenericArguments.player(Text.of("player")), GenericArguments.doubleNum(Text.of("amount")))
        .build();
  }
}
