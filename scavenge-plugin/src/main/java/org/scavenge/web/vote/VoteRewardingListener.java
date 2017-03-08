package org.scavenge.web.vote;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;
import org.scavenge.ScavengePlugin;
import org.scavenge.economy.base.SCDollar;
import org.scavenge.economy.base.SCEconomyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class VoteRewardingListener implements VoteListener {
  private final SCEconomyService service;

  public VoteRewardingListener(SCEconomyService economyService) {
    this.service = economyService;
  }

  @Override
  public void voteMade(Vote vote) {
    Optional<Player> player = Sponge.getServer().getPlayer(vote.getUsername());
    String message = vote.getUsername() + " /vote'd and got rewarded!";
    if (!player.isPresent()) {
      message = vote.getUsername() + " /vote'd but wasn't online for the rewards.";
      Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.GOLD, message));
      return;
    }
    Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.GOLD, message));
    this.service.getOrCreateAccount(player.get().getUniqueId()).get().deposit(SCDollar.instance, BigDecimal.valueOf(100),
        Cause.source(ScavengePlugin.instance).build());
    player.get().sendMessage(Text.of(TextColors.GOLD, "You've been given $100."));
  }
}
