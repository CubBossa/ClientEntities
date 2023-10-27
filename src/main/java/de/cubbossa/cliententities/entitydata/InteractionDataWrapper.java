package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class InteractionDataWrapper extends EntityDataWrapper {

  protected InteractionDataWrapper() {
  }

  public static EntityData width(float width) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, width) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        return 8;
      }
    };
  }

  public static EntityData height(float height) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, height) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        return 9;
      }
    };
  }

  public static EntityData responsive(boolean responsive) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BOOLEAN, responsive) {

      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        return 10;
      }
    };
  }
}