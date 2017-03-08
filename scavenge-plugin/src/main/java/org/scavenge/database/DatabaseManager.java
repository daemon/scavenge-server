package org.scavenge.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
  private final ComboPooledDataSource dataSource;
  public DatabaseManager(String url, String username, String password) throws PropertyVetoException {
    this.dataSource = new ComboPooledDataSource();
    this.dataSource.setDriverClass("org.postgresql.Driver");
    this.dataSource.setJdbcUrl(url);
    this.dataSource.setUser(username);
    this.dataSource.setPassword(password);
    this.dataSource.setMaxPoolSize(8);
    this.dataSource.setMinPoolSize(2);
    this.dataSource.setMaxStatements(128);
    this.dataSource.setMaxStatementsPerConnection(16);
    this.dataSource.setAcquireRetryAttempts(10);
    this.dataSource.setAcquireIncrement(2);
    this.dataSource.setMaxIdleTime(1800);
    this.dataSource.setIdleConnectionTestPeriod(1500);
    this.dataSource.setTestConnectionOnCheckin(true);
  }

  public Connection getConnection() throws SQLException {
    return this.dataSource.getConnection();
  }

  public void initDatabase() {
    InputStream stream = this.getClass().getResourceAsStream("/init.sql");
    try (SqlStreamExecutor executor = new SqlStreamExecutor(this.getConnection(), stream)) {
      executor.execute();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }
}
