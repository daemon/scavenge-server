package org.scavenge.economy.trade.net;

import org.scavenge.economy.trade.VirtualAccountInventory;
import org.scavenge.web.RemoteActionServer;

import java.util.UUID;

public class LockInventoryCallback implements RemoteActionServer.Callback {
  @Override
  public void call(String data) {
    UUID uuid = UUID.fromString(data.trim());
    VirtualAccountInventory.blockUuid(uuid);
  }
}
