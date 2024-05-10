package de.cubbossa.cliententities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityIdProviderTest {

  @Test
  public void multipleInstances() {
    EntityIdProvider a = new EntityIdProvider();
    EntityIdProvider b = new EntityIdProvider();
    EntityIdProvider c = new EntityIdProvider();

    int before = a.nextEntityId();
    b.nextEntityId();
    int after = c.nextEntityId();

    Assertions.assertEquals(before + 2, after);
  }

}
