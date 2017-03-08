package org.scavenge.lag;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.Collection;
import java.util.Iterator;

public class SpawnPreventListener {
  @Listener
  public void onSpawnEvent(SpawnEntityEvent event) {
    for (Entity entity : event.getEntities()) {
      Collection<Entity> entities = entity.getNearbyEntities(16);
      int entitiesNearby = entity.getNearbyEntities(16).size();
      int livingNearby = 0;
      Iterator<Entity> entityIterator = entities.iterator();
      int i = 0;
      while (entityIterator.hasNext()) {
        Entity e = entityIterator.next();
        ++i;
        if (e instanceof Living)
          ++livingNearby;
        if (i > 250)
          break;
      }

      if (entitiesNearby > 200 && entity instanceof Living && !(entity instanceof Human) &&
          !(entity instanceof Player))
        event.setCancelled(true);
      return;
    }
  }
}
