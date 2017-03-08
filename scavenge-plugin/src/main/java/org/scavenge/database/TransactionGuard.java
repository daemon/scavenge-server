package org.scavenge.database;


import java.sql.Connection;
import java.sql.SQLException;

public class TransactionGuard<T> implements AutoCloseable, SQLRunnable {
  private final Connection connection;
  private static final int DEADLOCK_RETRY_TIMES = 3;
  private final SQLRunnable<T> runnable;
  private boolean doRollback = false;

  public TransactionGuard(Connection connection, SQLRunnable<T> runnable) throws SQLException {
    connection.setAutoCommit(false);
    this.connection = connection;
    this.runnable = runnable;
  }

  @Override
  public void close() throws SQLException {
    try {
      if (this.doRollback) {
        this.connection.rollback();
        return;
      }
      this.connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
      this.connection.rollback();
      throw e;
    } finally {
      this.connection.setAutoCommit(true);
    }
  }

  private T run(int retryCount) throws Exception {
    try {
      return this.runnable.run();
    } catch (Exception e) {
      if (e instanceof SQLException) {
        SQLException sqlException = (SQLException) e;
        if (sqlException.getSQLState().equals("40P01") && retryCount < DEADLOCK_RETRY_TIMES) {
          ++retryCount;
          return this.run(retryCount + 1);
        }
      }
      this.doRollback = true;
      throw e;
    }
  }

  @Override
  public T run() throws Exception {
    return this.run(0);
  }
}
