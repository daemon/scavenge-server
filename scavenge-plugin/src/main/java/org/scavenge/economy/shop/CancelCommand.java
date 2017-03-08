package org.scavenge.economy.shop;

import org.scavenge.economy.shop.builder.ShopChatBuilder;
import org.scavenge.economy.shop.builder.ShopChatManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.TreeMap;

public class CancelCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player))
      return CommandResult.empty();
    ShopChatBuilder builder = ShopChatManager.instance.get((Player) src);
    if (builder == null)
      return CommandResult.success();
    builder.finish();
    return CommandResult.success();
  }

  public static CommandSpec createSpec() {
    return CommandSpec.builder()
        .description(Text.of("Cancels the current context."))
        .permission("scavenge.cmd.cancel")
        .executor(new CancelCommand())
        .build();
  }
}
