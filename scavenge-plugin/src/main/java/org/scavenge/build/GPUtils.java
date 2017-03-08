package org.scavenge.build;

import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import me.ryanhamshire.griefprevention.api.claim.TrustType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class GPUtils {
  public static boolean canBuild(Player player, Location<World> location) {
    Claim claim = GriefPrevention.getApi().getClaimManager(location.getExtent()).getClaimAt(location, false);
    if (claim.getType().equals(ClaimType.WILDERNESS))
      return true;
    return claim.getOwnerUniqueId().equals(player.getUniqueId()) ||
        claim.getTrusts(TrustType.BUILDER).contains(player.getUniqueId());
  }
}
