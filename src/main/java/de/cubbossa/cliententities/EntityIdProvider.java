package de.cubbossa.cliententities;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public class EntityIdProvider implements EntityIdGenerator {

  private static final EntityIdGenerator provider = new EntityIdProvider();

  public static EntityIdGenerator get() {
    return provider;
  }

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private static final String propertyKey = "minecraft.cliententities.entityid";

  public EntityIdProvider() {
    // put a start id
    System.setProperty(propertyKey, Integer.toString((int) 1e5));
  }

  @Override
  public int nextEntityId() {
    lock.writeLock().lock();
    try {
      int current = Integer.parseInt(System.getProperty(propertyKey));
      current = (current + 1) % Integer.MAX_VALUE;
      System.setProperty(propertyKey, current + "");
      return current;
    } finally {
      lock.writeLock().unlock();
    }
  }
}
