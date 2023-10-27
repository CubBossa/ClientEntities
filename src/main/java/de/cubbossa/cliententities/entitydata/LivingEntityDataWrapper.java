package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

public class LivingEntityDataWrapper {

  protected LivingEntityDataWrapper() {}

  public static EntityData handState(boolean handActive, boolean mainHand, boolean spinAttack) {
    return handState((byte) ((handActive ? 0x01 : 0) | (mainHand ? 0 : 0x02) | (spinAttack ? 0x04 : 0)));
  }

  public static EntityData handState(byte byteMask) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, byteMask) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8)) {
          return -1;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
          return 5;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_11)) {
          return 6;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
          return 7;
        }
        return 8;
      }
    };
  }

  public static EntityData health(float health) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, health) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8)) {
          return 6;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_11)) {
          return 7;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
          return 8;
        }
        return 9;
      }
    };
  }

  public static EntityData potionEffectColor(Color color) {
    return potionEffectColor(color.getRGB());
  }

  public static EntityData potionEffectColor(int rgb) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, rgb) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8)) {
          return 7;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_11)) {
          return 8;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
          return 9;
        }
        return 10;
      }
    };
  }

  public static EntityData potionEffectAmbient(boolean ambient) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BOOLEAN, ambient) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8)) {
          return 8;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_11)) {
          return 9;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
          return 10;
        }
        return 11;
      }
    };
  }

  public static EntityData numberOfArrowsInEntity(int count) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, count) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8)) {
          return 9;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_11)) {
          return 10;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
          return 11;
        }
        return 12;
      }
    };
  }

  public static EntityData numberOfBeeStingersInEntity(int count) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, count) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
          return 13;
        }
        return -1;
      }
    };
  }

  public static EntityData noBedLocation() {
    return bedLocation(null);
  }

  public static EntityData bedLocation(@Nullable Location location) {
    return new AbstractEntityDataWrapper(EntityDataTypes.OPTIONAL_BLOCK_POSITION, Optional.ofNullable(location)) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
          return 14;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
          return 13;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
          return 12;
        }
        return -1;
      }
    };
  }
}
