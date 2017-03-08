package org.scavenge.database;

@FunctionalInterface
public interface SQLRunnable<T> {
  T run() throws Exception;
}
