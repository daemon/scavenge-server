package org.scavenge.economy.item;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class RegisterItemCommand implements CommandExecutor {
  private final ItemDatabase itemDb;

  private RegisterItemCommand(ItemDatabase itemDb) {
    this.itemDb = itemDb;
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = (Player) src;
    ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
    try {
      this.itemDb.registerItem(itemStack);
    } catch (SQLException e) {
      throw new CommandException(Text.of("Database failure"));
    }
    return CommandResult.success();
  }

  public static CommandSpec createSpec(ItemDatabase itemDb) {
    return CommandSpec.builder()
        .description(Text.of("Registers an item for trade"))
        .permission("scavenge.admin")
        .executor(new RegisterItemCommand(itemDb))
        .build();

  }
}
