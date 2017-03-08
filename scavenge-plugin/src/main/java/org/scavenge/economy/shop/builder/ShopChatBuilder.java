package org.scavenge.economy.shop.builder;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public abstract class ShopChatBuilder {
  private final Player player;
  private boolean running = true;
  private static Text CANCEL_COMMAND = Text.of(TextColors.AQUA, "/cancel");

  public ShopChatBuilder(Player player) throws IllegalStateException {
    this.player = player;
    player.sendMessage(Text.of(TextColors.GOLD, "Public chat has been turned off for text input. Do ", CANCEL_COMMAND,
        TextColors.GOLD, " to exit."));
    ShopChatManager.instance.registerBuilder(this);
  }

  public boolean isRunning() {
    return this.running;
  }

  public void finish() {
    ShopChatManager.instance.removePlayer(this.player);
    this.running = false;
    player.sendMessage(Text.of(TextColors.GOLD, "Public chat has been turned back on."));
  }

  public Player player() {
    return this.player;
  }

  public void onTextInput(String text) {
    this.player.sendMessage(Text.of(TextStyles.ITALIC, text));
    this.receiveTextInput(text);
  }

  public void receiveTextInput(String text) {}
  public void receiveClickBlockInput(Location<World> location) {}
}
