package de.cubbossa.cliententities;

public class TrackedBoolField extends TrackedField<Boolean> {

  public TrackedBoolField() {
    super(false);
  }

  public TrackedBoolField(boolean init) {
    super(init);
  }

  public boolean getBooleanValue() {
    return Boolean.TRUE.equals(super.getValue());
  }
}
