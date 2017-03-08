package org.scavenge.lag;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

public class NearbyEntitiesCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    String option = args.<String>getOne("option").get();
    int radius = args.<Integer>getOne("radius").get();
    Player player = (Player) src;
    if (option.equals("count")) {
      player.sendMessage(Text.of(TextColors.GOLD, "Count: ", player.getNearbyEntities(radius).size() - 1));
    } else if (option.equals("remove")) {
      for (Entity entity : player.getNearbyEntities(radius))
        if (!(entity instanceof Player))
          entity.remove();
    }
    return CommandResult.success();
  }

  public static CommandSpec createSpec() {
    Map<String, String> options = new HashMap<>();
    options.put("count", "count");
    options.put("remove", "remove");
    return CommandSpec.builder().description(Text.of("Nearby entities tools."))
        .permission("scavenge.cmd.nearbyentities")
        .arguments(GenericArguments.choices(Text.of("option"), options), GenericArguments.onlyOne(GenericArguments.integer(Text.of("radius"))))
        .executor(new NearbyEntitiesCommand())
        .build();
  }
}
