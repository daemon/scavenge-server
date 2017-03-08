package org.scavenge.inspect;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;

public class InspectBlockCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    Map<DataQuery, Object> data = player.getLocation().add(0, -1, 0).getBlock().toContainer().getValues(true);
    player.sendMessage(Text.of(player.getLocation().getBlockType().getName()));
    data.forEach((d, o) -> player.sendMessage(Text.of(d.toString(), " : ", o.toString())));
    return CommandResult.success();
  }

  public static CommandSpec createSpec() {
    return CommandSpec.builder().description(Text.of("Inspects the current item."))
        .executor(new InspectBlockCommand())
        .permission("scavenge.admin")
        .build();
  }
}

