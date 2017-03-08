package org.scavenge.ranks;

import org.scavenge.economy.base.UserDatabase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FriendCommand implements CommandExecutor {
  private final UserDatabase db;

  public FriendCommand(UserDatabase db) {
    this.db = db;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    int nFriends = 0;
    try {
      nFriends = this.db.getFriendCount(player.getUniqueId());
    } catch (Exception e) {
      e.printStackTrace();
      return CommandResult.empty();
    }
    if (nFriends < 1) {
      try {
        this.db.addFriend(player.getUniqueId());
      } catch (Exception e) {
        e.printStackTrace();
        return CommandResult.empty();
      }
      Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "whitelist add " + args.<String>getOne("friend").get());
      src.sendMessage(Text.of(TextColors.GOLD, "You have used your friend pass."));
    } else {
      src.sendMessage(Text.of(TextColors.RED, "You've already used your friend pass!"));
    }
    return CommandResult.success();
  }

  public static CommandSpec createSpec(UserDatabase db) {
    return CommandSpec.builder().arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("friend"))))
        .description(Text.of("Add a friend"))
        .executor(new FriendCommand(db))
        .permission("scavenge.cmd.addfriend")
        .build();
  }
}
