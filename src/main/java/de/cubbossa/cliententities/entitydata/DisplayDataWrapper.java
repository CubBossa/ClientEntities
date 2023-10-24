package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import org.bukkit.entity.Display;

import java.awt.*;

public class DisplayDataWrapper extends EntityDataWrapper {

  protected DisplayDataWrapper() {
  }

  public static EntityData interpolationDelay(int delay) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, delay) {
      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        return 8;
      }
    };
  }

  public static EntityData interpolationDuration(int duration) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, duration) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        return 9;
      }
    };
  }

  public static EntityData posRosInterpolationDuration(int duration) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, duration) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_20_2)) {
          return -1;
        }
        return 10;
      }
    };
  }

  public static EntityData translation(Vector3f translation) {
    return new AbstractEntityDataWrapper(EntityDataTypes.VECTOR3F, translation) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 11;
        }
        return 10;
      }
    };
  }

  public static EntityData scale(Vector3f scale) {
    return new AbstractEntityDataWrapper(EntityDataTypes.VECTOR3F, scale) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 12;
        }
        return 11;
      }
    };
  }

  public static EntityData leftRotation(Quaternion4f rotation) {
    return new AbstractEntityDataWrapper(EntityDataTypes.QUATERNION, rotation) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 13;
        }
        return 12;
      }
    };
  }

  public static EntityData rightRotation(Quaternion4f rotation) {
    return new AbstractEntityDataWrapper(EntityDataTypes.QUATERNION, rotation) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 14;
        }
        return 13;
      }
    };
  }

  public static EntityData billboardConstraints(Display.Billboard billboard) {
    return billboardConstraints((byte) billboard.ordinal());
  }

  public static EntityData billboardConstraints(byte billboardIndex) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, billboardIndex) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 15;
        }
        return 14;
      }
    };
  }

  public static EntityData brightnessOverride() {
    return brightnessOverride(-1);
  }

  public static EntityData brightnessOverride(int blockLight, int skyLight) {
    return brightnessOverride(blockLight << 4 | skyLight << 20);
  }

  public static EntityData brightnessOverride(int lightValue) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, lightValue) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 16;
        }
        return 15;
      }
    };
  }

  public static EntityData viewRange(float chunks) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, chunks) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 17;
        }
        return 16;
      }
    };
  }

  public static EntityData shadowRadius(float size) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, size) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 18;
        }
        return 17;
      }
    };
  }

  public static EntityData shadowStrength(float strength) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, strength) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 19;
        }
        return 18;
      }
    };
  }

  public static EntityData width(float width) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, width) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 20;
        }
        return 19;
      }
    };
  }

  public static EntityData height(float height) {
    return new AbstractEntityDataWrapper(EntityDataTypes.FLOAT, height) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 21;
        }
        return 20;
      }
    };
  }

  public static EntityData glowColorOverride() {
    return glowColorOverride(-1);
  }

  public static EntityData glowColorOverride(Color color) {
    return glowColorOverride(color.getRGB());
  }

  public static EntityData glowColorOverride(org.bukkit.Color color) {
    return glowColorOverride(color.asARGB());
  }

  public static EntityData glowColorOverride(int rgb) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, rgb) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 22;
        }
        return 21;
      }
    };
  }
}
