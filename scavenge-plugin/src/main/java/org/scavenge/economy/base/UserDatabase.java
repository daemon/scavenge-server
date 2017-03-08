package org.scavenge.economy.base;

import org.scavenge.database.DatabaseManager;
import org.scavenge.database.TransactionGuard;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDatabase {
  private final DatabaseManager manager;

  public UserDatabase(DatabaseManager manager) {
    this.manager = manager;
  }

  public void upgradeRank(UUID uuid, int cost) throws SQLException {
    String stmtTxt = "UPDATE scavenge_user SET rank=rank+1 WHERE uuid=uuid(?)";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(stmtTxt)) {
      stmt.setString(1, uuid.toString());
      stmt.execute();
    }
  }

  public void removeAccountTrial(UUID uuid) throws Exception {
    String updateTxt = "UPDATE scavenge_user SET expiry='2099-01-01'::timestamp WHERE uuid=uuid(?)";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(updateTxt)) {
      stmt.setString(1, uuid.toString());
      stmt.execute();
    }
  }

  public void addFriend(UUID uuid) throws Exception {
    String updateTxt = "UPDATE scavenge_user SET n_friends=n_friends+1 WHERE uuid=uuid(?)";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(updateTxt)) {
      stmt.setString(1, uuid.toString());
      stmt.executeUpdate();
    }
  }

  public int getFriendCount(UUID uuid) throws Exception {
    String stmtTxt = "SELECT n_friends FROM scavenge_user WHERE uuid=uuid(?)";
    try (Connection conn = this.manager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(stmtTxt)) {
      stmt.setString(1, uuid.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next())
          return rs.getInt(1);
      }
    }
    return 0;
  }

  public SCUniqueAccount getOrCreateAccount(UUID uuid, String displayName, boolean isTrial) throws Exception {
    String stmtTxt = "SELECT * FROM scavenge_user WHERE uuid=uuid(?)";
    if (!isTrial) {
      String insStmtTxt = "INSERT INTO scavenge_user (uuid, name) VALUES (uuid(?), ?)";
      try (Connection conn = this.manager.getConnection();
           TransactionGuard<SCUniqueAccount> guard = new TransactionGuard<>(conn, () -> {
             try (PreparedStatement stmt = conn.prepareStatement(stmtTxt)) {
               stmt.setString(1, uuid.toString());
               try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next())
                   return new SCUniqueAccount(this, uuid.toString(), rs.getString(2), rs.getDouble(3), rs.getInt(5));
               }
             }
             try (PreparedStatement stmt = conn.prepareStatement(insStmtTxt)) {
               stmt.setString(1, uuid.toString());
               stmt.setString(2, displayName);
               stmt.execute();
               return new SCUniqueAccount(this, uuid.toString(), displayName, 0, 0);
             }
           })) {
        return guard.run();
      }
    } else {
      String insStmtTxt = "INSERT INTO scavenge_user (uuid, name, expiry) VALUES (uuid(?), ?, to_timestamp(?))";
      try (Connection conn = this.manager.getConnection();
           TransactionGuard<SCUniqueAccount> guard = new TransactionGuard<>(conn, () -> {
             try (PreparedStatement stmt = conn.prepareStatement(stmtTxt)) {
               stmt.setString(1, uuid.toString());
               try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next())
                   return new SCUniqueAccount(this, uuid.toString(), rs.getString(2), rs.getDouble(3), rs.getInt(5));
               }
             }
             try (PreparedStatement stmt = conn.prepareStatement(insStmtTxt)) {
               stmt.setString(1, uuid.toString());
               stmt.setString(2, displayName);
               stmt.setLong(3, System.currentTimeMillis() / 1000 + 7 * 24 * 3600);
               stmt.execute();
               return new SCUniqueAccount(this, uuid.toString(), displayName, 0, 0);
             }
           })) {
        return guard.run();
      }
    }
  }

  public ResultType setBalance(String uuid, double bal) {
    String stmtTxt = "UPDATE scavenge_user SET money=? WHERE uuid=uuid(?)";
    try {
      try (Connection conn = this.manager.getConnection();
           PreparedStatement stmt = conn.prepareStatement(stmtTxt)) {
        stmt.setDouble(1, bal);
        stmt.setString(2, uuid);
        stmt.execute();
      }
    } catch (SQLException e) {
      return ResultType.FAILED;
    }
    return ResultType.SUCCESS;
  }

  public ResultType transfer(String fromUuid, String toUuid, double amount) {
    String stmtTxt = "UPDATE scavenge_user SET money=money-? WHERE uuid=uuid(?)";
    String stmtTxt2 = "UPDATE scavenge_user SET money=money+? WHERE uuid=uuid(?)";
    try (Connection conn = this.manager.getConnection();
         TransactionGuard<ResultType> guard = new TransactionGuard<>(conn, () -> {
           try (PreparedStatement stmt = conn.prepareStatement(stmtTxt)) {
             stmt.setDouble(1, amount);
             stmt.setString(2, fromUuid);
             int rows = 0;
             try {
               rows = stmt.executeUpdate();
             } catch (SQLException e) {
               return ResultType.ACCOUNT_NO_FUNDS;
             }
             if (rows == 0)
               return ResultType.FAILED;
           }
           try (PreparedStatement stmt = conn.prepareStatement(stmtTxt2)) {
             stmt.setDouble(1, amount);
             stmt.setString(2, toUuid);
             int rows = stmt.executeUpdate();
             if (rows == 0) {
               conn.rollback();
               return ResultType.FAILED;
             }
           }
           return ResultType.SUCCESS;
         })) {
      return guard.run();
    } catch (Exception e) {
      return ResultType.FAILED;
    }
  }
}
