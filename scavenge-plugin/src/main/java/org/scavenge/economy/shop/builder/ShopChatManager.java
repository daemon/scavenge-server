package org.scavenge.economy.shop.builder;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.*;

public class ShopChatManager {
  public static ShopChatManager instance = new ShopChatManager();
  private Map<Player, ShopChatBuilder> currentPlayers = new HashMap<>();
  private Map<Player, MessageChannel> removedChannels = new HashMap<>();
  private ShopChatManager() {}

  public void registerBuilder(ShopChatBuilder builder) throws IllegalStateException {
    Player player = builder.player();
    if (this.currentPlayers.containsKey(player) && this.currentPlayers.get(player).isRunning())
      throw new IllegalStateException("Player currently using shop editor");
    this.currentPlayers.put(player, builder);
  }

  public ShopChatBuilder get(Player player) {
    return this.currentPlayers.get(player);
  }

  public void removePlayer(Player player) {
    this.currentPlayers.remove(player);
    MessageChannel channel = this.removedChannels.get(player);
    if (channel != null)
      channel.asMutable().addMember(player);
  }

  @Listener(order=Order.LATE)
  public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
    Player player = event.getTargetEntity();
    this.removePlayer(player);
  }

  @Listener(order=Order.FIRST)
  public void onMessageEvent(MessageChannelEvent.Chat event) {
    Optional<MessageChannel> channel = event.getChannel();
    Optional<Player> player = event.getCause().get("Source", Player.class);
    if (channel.isPresent() && player.isPresent()) {
      for (Player p : this.currentPlayers.keySet()) {
        if (!this.currentPlayers.get(p).isRunning())
          continue;
        channel.get().asMutable().removeMember(p);
        this.removedChannels.put(p, channel.get());
      }
    }
    if (!player.isPresent())
      return;
    Player p2 = player.get();
    if (!currentPlayers.containsKey(p2) || !currentPlayers.get(p2).isRunning()) {
      return;
    }
    ShopChatBuilder builder = currentPlayers.get(p2);
    builder.onTextInput(event.getRawMessage().toPlain());
    event.setCancelled(true);
  }

  @Listener(order=Order.LAST)
  public void onClickEvent(InteractBlockEvent event) {
    Player player = event.getCause().first(Player.class).get();
    ShopChatBuilder builder = this.currentPlayers.get(player);
    if (builder == null)
      return;
    builder.receiveClickBlockInput(event.getTargetBlock().getLocation().get());
  }
}
