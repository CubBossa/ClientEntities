package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import net.kyori.adventure.text.Component;

import java.awt.*;

public class TextDisplayDataWrapper extends DisplayDataWrapper {

  protected TextDisplayDataWrapper() {
  }

  public static class Text extends AbstractEntityDataWrapper {
    public Text(Component text) {
      this(SERIALIZER.serialize(text));
    }

    public Text(String component) {
      super(EntityDataTypes.COMPONENT, component);
    }

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
  }

  public static class TextWidth extends AbstractEntityDataWrapper {
    public TextWidth(int width) {
      super(EntityDataTypes.INT, width);
    }

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
  }

  public static class BackgroundColor extends AbstractEntityDataWrapper {
    public BackgroundColor() {
      this(0x40000000);
    }

    public BackgroundColor(Color color) {
      this(color.getRGB());
    }

    public BackgroundColor(int rgba) {
      super(EntityDataTypes.INT, rgba);
    }

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
  }

  public static class TextOpacity extends AbstractEntityDataWrapper {
    public TextOpacity() {
      this((byte) -1);
    }

    public TextOpacity(byte opacity) {
      super(EntityDataTypes.BYTE, opacity);
    }

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
  }

  public static class Options extends AbstractEntityDataWrapper {
    public Options(boolean hasShadow, boolean canSeeThrough, boolean defaultBackground, TextAlignment alignment) {
      this((byte) ((hasShadow ? 0x01 : 0)
          | (canSeeThrough ? 0x02 : 0)
          | (defaultBackground ? 0x04 : 0)
          | (switch (alignment) {
        case CENTER -> 0x00;
        case LEFT -> 0x08;
        case RIGHT -> 0x10;
      })));
    }

    public Options(byte bitmask) {
      super(EntityDataTypes.BYTE, bitmask);
    }


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
  }

  public enum TextAlignment {
    CENTER, LEFT, RIGHT
  }
}
