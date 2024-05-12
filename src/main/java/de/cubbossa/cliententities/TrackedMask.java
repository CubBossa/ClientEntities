package de.cubbossa.cliententities;

import java.util.HashMap;
import java.util.Map;

public class TrackedMask {

  Map<TrackedField<Boolean>, Byte> fields;

  public TrackedMask(TrackedField<Boolean>... fields) {
    this.fields = new HashMap<>();
    byte i = 0;
    for (TrackedField<Boolean> field : fields) {
      byte val = (byte) Math.pow(2, i++);
      if (field != null) {
        this.fields.put(field, val);
      }
    }
  }

  public TrackedMask(Map<TrackedField<Boolean>, Byte> fieldByteMap) {
    this.fields = new HashMap<>(fieldByteMap);
  }

  public boolean hasChanged() {
    return this.fields.keySet().stream().anyMatch(TrackedField::hasChanged);
  }

  public byte byteVal() {
    return fields.entrySet().stream()
        .filter(e -> e.getKey().getValue())
        .map(Map.Entry::getValue)
        .reduce((a, b) -> (byte) (a | b))
        .orElse((byte) 0);
  }
}
