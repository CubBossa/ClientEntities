package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import net.kyori.adventure.text.Component;

import java.awt.*;

public class TextDisplayDataWrapper extends DisplayDataWrapper {

  protected TextDisplayDataWrapper() {
  }

  public static EntityData text(Component text) {
    return text(SERIALIZER.serialize(text));
  }

  public static EntityData text(String component) {
    return new AbstractEntityDataWrapper(EntityDataTypes.COMPONENT, component) {

      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 23;
        }
        return 22;
      }
    };
  }

  public static EntityData textWidth(int width) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, width) {

      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 24;
        }
        return 23;
      }
    };
  }

  public static EntityData backgroundColor(Color color) {
    return backgroundColor(color.getRGB());
  }

  public static EntityData backgroundColor(org.bukkit.Color color) {
    return backgroundColor(color.asARGB());
  }

  public static EntityData backgroundColor() {
    return backgroundColor(0x40000000);
  }

  public static EntityData backgroundColor(int argb) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, argb) {

      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 25;
        }
        return 24;
      }
    };
  }

  public static EntityData textOpacity() {
    return textOpacity((byte) -1);
  }

  public static EntityData textOpacity(byte opacity) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, opacity) {

      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 26;
        }
        return 25;
      }
    };
  }

  public static EntityData options(boolean hasShadow, boolean canSeeThrough, boolean defaultBackground, TextAlignment alignment) {
    return options((byte) ((hasShadow ? 0x01 : 0)
        | (canSeeThrough ? 0x02 : 0)
        | (defaultBackground ? 0x04 : 0)
        | (switch (alignment) {
      case CENTER -> 0x00;
      case LEFT -> 0x08;
      case RIGHT -> 0x10;
    })));
  }

  public static EntityData options(int bitmask) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, bitmask) {

      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 27;
        }
        return 26;
      }
    };
  }

    public enum TextAlignment {
      CENTER, LEFT, RIGHT
    }
  }
