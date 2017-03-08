package org.scavenge.ranks;

import org.scavenge.economy.base.SCUniqueAccount;
import org.scavenge.economy.base.UserDatabase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class RankCommand implements CommandExecutor {
  private final UserDatabase userDb;

  public RankCommand(UserDatabase userDb) {
    this.userDb = userDb;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    SCUniqueAccount account = null;
    try {
      account = this.userDb.getOrCreateAccount(player.getUniqueId(), player.getName(), false);
    } catch (Exception e) {
      return CommandResult.empty();
    }
    player.sendMessage(Text.of(TextColors.GOLD, "Purchasable ranks:"));
    for (int i = account.rank().ordinal() + 1; i < Rank.values().length; ++i)
      player.sendMessage(Text.of(TextColors.AQUA, Rank.values()[i].name, TextColors.GOLD, ": $", Rank.values()[i].cost));
    player.sendMessage(Text.of(TextColors.AQUA, "/ranks buy (name)"));
    return CommandResult.success();
  }
}
