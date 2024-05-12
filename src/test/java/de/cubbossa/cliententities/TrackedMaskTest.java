package de.cubbossa.cliententities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TrackedMaskTest {

  @Test
  public void test1() {
    TrackedBoolField a = new TrackedBoolField();
    TrackedBoolField b = new TrackedBoolField(true);
    TrackedBoolField c = new TrackedBoolField(true);
    TrackedMask mask = new TrackedMask(a, b, null, c);
    assertEquals(0x2 | 0x8, mask.byteVal());
    a.setValue(true);
    assertTrue(mask.hasChanged());
    assertEquals(0x1 | 0x2 | 0x8, mask.byteVal());
  }
}