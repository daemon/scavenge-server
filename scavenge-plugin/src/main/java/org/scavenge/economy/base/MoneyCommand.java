package org.scavenge.economy.base;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MoneyCommand implements CommandExecutor {
  private final SCEconomyService service;

  public MoneyCommand(SCEconomyService service) {
    this.service = service;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    UniqueAccount account = this.service.getOrCreateAccount(player.getUniqueId()).get();
    double balance = Math.round(account.getBalance(SCDollar.instance).doubleValue() * 100.0) / 100.0;
    player.sendMessage(Text.of(TextColors.GREEN, "Current balance: $", balance));
    return CommandResult.success();
  }

  public static CommandSpec createSpec(SCEconomyService service) {
    return CommandSpec.builder().description(Text.of("Gets current balance in bank."))
        .executor(new MoneyCommand(service))
        .permission("scavenge.cmd.money")
        .build();
  }
}
