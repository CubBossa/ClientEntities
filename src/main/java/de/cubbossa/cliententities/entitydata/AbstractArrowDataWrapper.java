package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class AbstractArrowDataWrapper extends EntityDataWrapper {

  public static EntityData options(boolean critical, boolean noClip) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, false) {

      @Override
      public Object getValue() {
        return (critical ? 0x1 : 0) | (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13) && noClip ? 0x2 : 0);
      }

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8)) {
          return 16;
        }
        if (this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_9)) {
          return 5;
        }
        if (this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_12)) {
          return 6;
        }
        if (this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_17)) {
          return 7;
        }
        return 8;
      }
    };
  }

  public static EntityData piercingLevel(int level) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, level) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
          return 9;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
          return 10;
        }
        return -1;
      }
    };
  }
}
