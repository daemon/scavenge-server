package org.scavenge.economy.trade;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class TradeCommand implements CommandExecutor {
  private final TradeDatabase tradeDb;

  public TradeCommand(TradeDatabase tradeDb) {
    this.tradeDb = tradeDb;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    new VirtualAccountInventory(this.tradeDb, player).open();
    return CommandResult.success();
  }

  public static CommandSpec createSpec(TradeDatabase database) {
    return CommandSpec.builder().description(Text.of("Opens your online trading account inventory."))
        .permission("scavenge.cmd.trade")
        .executor(new TradeCommand(database))
        .build();
  }
}
