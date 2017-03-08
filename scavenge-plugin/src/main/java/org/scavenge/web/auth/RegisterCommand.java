package org.scavenge.web.auth;

import org.scavenge.gui.TextLink;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.net.MalformedURLException;

public class RegisterCommand implements CommandExecutor {
  private final RegistrationTokenStore tokenStore;

  public RegisterCommand() {
    this.tokenStore = new RegistrationTokenStore();
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player))
      return CommandResult.empty();
    Player player = (Player) src;
    String urlText = "http://scavenge.org/register?id=" + this.tokenStore.token(player);
    Text link = null;
    try {
      link = new TextLink(urlText).build();
    } catch (MalformedURLException e) {
      return CommandResult.empty();
    }
    player.sendMessage(Text.of("Your registration link: ").concat(link));
    return CommandResult.success();
  }
}
