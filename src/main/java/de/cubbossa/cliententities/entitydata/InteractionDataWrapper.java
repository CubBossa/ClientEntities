package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class InteractionDataWrapper extends EntityDataWrapper {

  protected  InteractionDataWrapper() {}

  public static class Width extends AbstractEntityDataWrapper {
    public Width(float width) {
      super(EntityDataTypes.FLOAT, width);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
        return -1;
      }
      return 8;
    }
  }

  public static class Height extends AbstractEntityDataWrapper {
    public Height(float height) {
      super(EntityDataTypes.FLOAT, height);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
        return -1;
      }
      return 9;
    }
  }

  public static class Responsive extends AbstractEntityDataWrapper {
    public Responsive(boolean responsive) {
      super(EntityDataTypes.BOOLEAN, responsive);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
        return -1;
      }
      return 10;
    }
  }
}
