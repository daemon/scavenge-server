package org.scavenge.economy.item;

import org.scavenge.database.DatabaseManager;
import org.spongepowered.api.item.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ItemDatabase {
  private final DatabaseManager manager;

  public ItemDatabase(DatabaseManager manager) {
    this.manager = manager;
  }

  public void registerItem(ItemStack item) throws SQLException {
    int dv = ItemUtils.damageValue(item);
    String id = ItemUtils.id(item);
    String displayName = ItemUtils.displayName(item);
    String stmt = "INSERT INTO scavenge_item (type_id, dv, display_name, max_stack) VALUES (?, ?, ?, ?)";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement ps = conn.prepareStatement(stmt)) {
      ps.setString(1, id);
      ps.setInt(2, dv);
      ps.setString(3, displayName.toLowerCase());
      ps.setInt(4, item.getMaxStackQuantity());
      ps.execute();
    }
  }

  public Item findItem(ItemStack item) throws SQLException {
    String findStmt = "SELECT * FROM scavenge_item WHERE type_id=? AND dv=?";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(findStmt)) {
      stmt.setString(1, ItemUtils.id(item));
      stmt.setInt(2, ItemUtils.damageValue(item));
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next())
          return new Item(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5));
        return null;
      }
    }
  }

  public Item findItem(String displayName) throws SQLException {
    String findStmt = "SELECT * FROM scavenge_item WHERE display_name=?";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(findStmt)) {
      stmt.setString(1, displayName);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next())
          return new Item(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5));
        return null;
      }
    }
  }

  public List<String> findItemsByDisplayPrefix(String prefix) throws SQLException {
    String findStmt = "SELECT display_name FROM scavenge_item WHERE display_name LIKE ? || '%'";
    List<String> matches = new LinkedList<>();
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(findStmt)) {
      stmt.setString(1, prefix.toLowerCase());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next())
          matches.add(rs.getString(1));
      }
    }
    return matches;
  }
}
