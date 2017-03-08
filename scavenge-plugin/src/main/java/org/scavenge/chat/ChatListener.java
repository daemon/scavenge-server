package org.scavenge.chat;

import org.scavenge.economy.base.SCDollar;
import org.scavenge.ranks.Rank;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.message.MessageChannelEvent.Chat;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener {
  private final Pattern pattern;
  private final EconomyService economyService;

  public ChatListener() {
    this.pattern = Pattern.compile("^<([^A-z]+)(.+?)> (.+?)$");
    this.economyService = Sponge.getServiceManager().provide(EconomyService.class).get();
  }

  @Listener(order=Order.LAST)
  public void onChatEvent(Chat event) throws Exception {
    Optional<MessageChannel> channel = event.getChannel();
    if (!channel.isPresent()) {
      return;
    }
    Optional<Player> player = event.getCause().get("Source", Player.class);
    /*Matcher matcher = this.pattern.matcher(event.getMessage().toPlain());
    if (!matcher.matches())
      return;
    String playerName = matcher.group(2);
    System.out.println(playerName);
    Optional<Player> player = Sponge.getGame().getServer().getPlayer(playerName);*/
    if (!player.isPresent())
      return;
    UUID uuid = player.get().getUniqueId();
    Optional<UniqueAccount> account = this.economyService.getOrCreateAccount(uuid);
    if (!account.isPresent())
      return;
    Text rank = null;
    try {
      rank = event.getMessage().getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0)
          .getChildren().get(1).getChildren().get(1).getChildren().get(0).getChildren().get(0);
    } catch (Exception e) {
      return;
    }
    Text netWorth = SCDollar.instance.format(account.get().getBalance(SCDollar.instance));
    Text hoverText = Text.of("Rank: " + Rank.find(rank).name, Text.NEW_LINE, "Net worth: ", netWorth);
    Text message = event.getMessage().getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0);
    Text name = Text.builder(player.get().getDisplayNameData().displayName().get().toPlain()).color(TextColors.AQUA)
        .onHover(TextActions.showText(hoverText)).build();
    Text line = Text.of(TextColors.LIGHT_PURPLE, "<", rank, name, TextColors.LIGHT_PURPLE, "> ", TextColors.WHITE, message);
    event.setMessage(line);
  }
}
