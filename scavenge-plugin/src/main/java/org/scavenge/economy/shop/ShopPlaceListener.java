package org.scavenge.economy.shop;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import java.util.List;
import java.util.Optional;

public class ShopPlaceListener {
  public void onBlockPlace(ChangeBlockEvent.Place event) {
    if (event.isCancelled())
      return;
    Optional<Player> player = event.getCause().first(Player.class);
    if (!player.isPresent())
      return;
    List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
    for (Transaction<BlockSnapshot> transaction : transactions) {
      System.out.println(transaction.getFinal().toContainer().get(DataQuery.of("test")).get());
    }
  }

  public void onRightClick(InteractItemEvent event) {

  }
}
