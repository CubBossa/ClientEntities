package de.cubbossa.cliententities;

import org.jetbrains.annotations.Nullable;

import java.io.LineNumberReader;
import java.util.Objects;

public class TrackedField<T> {

  boolean changed = false;
  T value;

  public TrackedField() {
    this(null);
  }

  public TrackedField(T init) {
    value = init;
  }

  public void overrideChanged(boolean changed) {
    this.changed = changed;
  }

  public boolean hasChanged() {
   return changed;
  }

  public void flushChanged() {
    changed = false;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    if (!Objects.equals(this.value, value)) {
      this.value = value;
      changed = true;
    }
  }
}
