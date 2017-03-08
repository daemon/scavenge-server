package org.scavenge.announce;

import org.scavenge.web.RemoteActionServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class AnnounceCallback implements RemoteActionServer.Callback {
  @Override
  public void call(String data) {
    Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.GOLD, data.trim()));
  }
}
