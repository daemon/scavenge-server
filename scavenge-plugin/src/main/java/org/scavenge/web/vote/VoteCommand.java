package org.scavenge.web.vote;

import org.scavenge.gui.TextLink;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.net.MalformedURLException;

public class VoteCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    try {
      src.sendMessage(new TextLink("http://ftbservers.com/server/6YQ2B2z7/vote").build());
    } catch (MalformedURLException e) {
      return CommandResult.empty();
    }
    return CommandResult.success();
  }

  public static CommandSpec createSpec() {
    return CommandSpec.builder().description(Text.of("Shows voting website"))
        .executor(new VoteCommand())
        .permission("scavenge.cmd.vote")
        .build();
  }
}
