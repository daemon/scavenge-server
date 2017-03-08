package org.scavenge.web;

import org.scavenge.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WebDatabase {
  private final DatabaseManager manager;
  public Set<UUID> uuidsWithAccounts = new HashSet<>();

  public WebDatabase(DatabaseManager manager) {
    this.manager = manager;
  }

  public boolean hasAccount(UUID uuid) throws SQLException {
    if (this.uuidsWithAccounts.contains(uuid))
      return true;
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM website_users WHERE uuid=uuid(?)")) {
      stmt.setString(1, uuid.toString());
      if (stmt.executeQuery().next()) {
        this.uuidsWithAccounts.add(uuid);
        return true;
      }
    }
    return false;
  }
}
