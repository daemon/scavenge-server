package org.scavenge.economy.shop;

import org.scavenge.economy.item.ItemDatabase;
import org.scavenge.economy.shop.builder.CreateShopChatBuilder;
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

public class ShopCommand implements CommandExecutor {
  private final ItemDatabase itemDb;
  private final ShopDatabase shopDb;

  enum CommandType {CREATE, HELP, DELETE}
  private ShopCommand(ItemDatabase itemDb, ShopDatabase shopDb) {
    this.itemDb = itemDb;
    this.shopDb = shopDb;
  }

  private CommandResult doCreate(Player player) throws CommandException {
    new CreateShopChatBuilder(player, this.itemDb, this.shopDb);
    return CommandResult.success();
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player))
      return CommandResult.empty();
    Player player = (Player) src;
    CommandType cmdType = args.<CommandType>getOne(Text.of("subcommand")).get();
    switch (cmdType) {
    case CREATE:
      return this.doCreate(player);
    case HELP:
      break;
    case DELETE:
      break;
    }
    return CommandResult.empty();
  }

  public static CommandSpec createSpec(ItemDatabase itemDb, ShopDatabase shopDb) {
    Map<String, CommandType> map = new TreeMap<>();
    map.put("create", CommandType.CREATE);
    map.put("delete", CommandType.DELETE);
    map.put("help", CommandType.HELP);
    return CommandSpec.builder()
        .description(Text.of("Base command for creating and modifying player chest shops."))
        .permission("scavenge.cmd.shop")
        .arguments(GenericArguments.onlyOne(GenericArguments.choices(Text.of("subcommand"), map)))
        .executor(new ShopCommand(itemDb, shopDb))
        .build();
  }
}
