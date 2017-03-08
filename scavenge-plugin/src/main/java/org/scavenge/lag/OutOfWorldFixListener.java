package org.scavenge.lag;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class OutOfWorldFixListener {
  @Listener
  public void onOutOfWorldDamage(DamageEntityEvent event) {
    if (!(event.getTargetEntity() instanceof Player))
      return;
    Player player = (Player) event.getTargetEntity();
    if (event.getTargetEntity().getLocation().getY() < 0) {
      Sponge.getCommandManager().process(player, "spawn");
      Sponge.getCommandManager().process(player, "home");
      Sponge.getCommandManager().process(player, "is home");
      event.setCancelled(true);
    }
  }
}
