package org.scavenge.inspect;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class InspectItemCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    DataContainer container = player.getItemInHand(HandTypes.MAIN_HAND).get().toContainer();
    container.getValues(true).keySet().forEach(
        query -> player.sendMessage(Text.of(query.toString(), " : ", container.get(query))));
    return CommandResult.success();
  }

  public static CommandSpec createSpec() {
    return CommandSpec.builder().description(Text.of("Inspects the current item."))
        .executor(new InspectItemCommand())
        .permission("scavenge.admin")
        .build();
  }
}
