package org.scavenge.economy.trade;

import org.scavenge.database.DatabaseManager;
import org.scavenge.database.TransactionGuard;
import org.scavenge.economy.item.Item;
import org.scavenge.economy.item.ItemUtils;
import org.scavenge.web.NoAccountException;
import org.scavenge.web.WebDatabase;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TradeDatabase {
  private final DatabaseManager manager;
  private final WebDatabase webDatabase;

  public TradeDatabase(DatabaseManager manager) {
    this.manager = manager;
    this.webDatabase = new WebDatabase(manager);
  }

  public List<ItemStack> fetchInventoryItems(Player player) throws NoAccountException, SQLException {
    if (!this.webDatabase.hasAccount(player.getUniqueId()))
      throw new NoAccountException();
    String fetchStmt = "SELECT * FROM scavenge_trading_inv JOIN scavenge_item ON scavenge_trading_inv.item_id=scavenge_item.id " +
        "WHERE player_uuid=uuid(?)";
    List<ItemStack> stacks = new LinkedList<>();
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(fetchStmt)) {
      stmt.setString(1, player.getUniqueId().toString());
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        Item item = new Item(rs.getInt("item_id"), rs.getString("type_id"), rs.getInt("dv"), rs.getString("display_name"), rs.getInt("max_stack"));
        Collections.addAll(stacks, item.toItemStacks(rs.getInt("quantity")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return stacks;
  }

  public boolean setInventory(Player player, Inventory inventory) {
    String itemStmt = "SELECT * FROM scavenge_item WHERE (type_id, dv) IN (";
    String delStmt = "DELETE FROM scavenge_trading_inv WHERE player_uuid=uuid(?)";
    String insStmt = "INSERT INTO scavenge_trading_inv (player_uuid, item_id, quantity) VALUES ";
    StringJoiner joiner = new StringJoiner(",");
    for (Inventory slot : inventory.slots())
      if (slot.peek().isPresent()) {
        joiner.add("(?, ?)");
      }
    boolean deleteOnly = false;
    if (joiner.toString().equals("")) {
      itemStmt = "SELECT * FROM scavenge_item WHERE 1=2";
      deleteOnly = true;
    } else
      itemStmt += joiner.toString() + ") FOR SHARE";
    Map<String, Map<Integer, ItemData>> itemData = new HashMap<>();
    final boolean finalDeleteOnly = deleteOnly;
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(itemStmt);
         TransactionGuard<Boolean> tg = new TransactionGuard<>(conn, () -> {
           int i = 1;
           for (Inventory slot : inventory.slots()) {
             if (!slot.peek().isPresent())
               continue;
             ItemStack item = slot.peek().get();
             if (!itemData.containsKey(ItemUtils.id(item))) {
               itemData.put(ItemUtils.id(item), new HashMap<>());
               itemData.get(ItemUtils.id(item)).put(ItemUtils.damageValue(item), new ItemData(-1, item.getQuantity()));
             }
             else if (itemData.get(ItemUtils.id(item)).containsKey(ItemUtils.damageValue(item)))
               itemData.get(ItemUtils.id(item)).get(ItemUtils.damageValue(item)).quantity += item.getQuantity();
             else
               itemData.get(ItemUtils.id(item)).put(ItemUtils.damageValue(item), new ItemData(-1, item.getQuantity()));
             stmt.setString(i, ItemUtils.id(item));
             stmt.setInt(i + 1, ItemUtils.damageValue(item));
             i += 2;
           }
           ResultSet rs = stmt.executeQuery();
           while (rs.next())
             itemData.get(rs.getString(2)).get(rs.getInt(3)).itemId = rs.getInt(1);
           try (PreparedStatement delStmt2 = conn.prepareStatement(delStmt)) {
             delStmt2.setString(1, player.getUniqueId().toString());
             delStmt2.execute();
           }
           if (finalDeleteOnly)
             return true;
           StringJoiner joiner2 = new StringJoiner(",");
           itemData.forEach((id, map) -> map.forEach((dv, data) -> {
             joiner2.add("(uuid(?), ?, ?)");
           }));
           String insStmt2 = insStmt + joiner2.toString();
           i = 1;
           try (PreparedStatement stmt2 = conn.prepareStatement(insStmt2)) {
             for (String id : itemData.keySet())
               for (Integer dv : itemData.get(id).keySet()) {
                 ItemData data = itemData.get(id).get(dv);
                 stmt2.setString(i, player.getUniqueId().toString());
                 stmt2.setInt(i + 1, data.itemId);
                 stmt2.setInt(i + 2, data.quantity);
                 i += 3;
               }
             stmt2.execute();
           }
           return true;
         })) {
          return tg.run();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private static class ItemData {
    private int itemId;
    private int quantity;

    public ItemData(int itemId, int quantity) {
      this.itemId = itemId;
      this.quantity = quantity;
    }
  }
}
