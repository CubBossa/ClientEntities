package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import java.awt.Color;

public class ArrowDataWrapper extends AbstractArrowDataWrapper {

  public static EntityData noParticles() {
    var other = particleColor(0);
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, 0) {
      @Override
      public Object getValue() {
        return serverVersion.isOlderThanOrEquals(ServerVersion.V_1_10) ? 0 : -1;
      }

      @Override
      protected int versionedIndex() {
        return other.getIndex();
      }
    };
  }

  public static EntityData particleColor(int color) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, color) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_8_8)) {
          return -1;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
          return 7;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
          return 8;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
          return 10;
        }
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
          return 9;
        }
        return 10;
      }
    };
  }

  public static EntityData particleColor(org.bukkit.Color color) {
    return particleColor(color.asRGB());
  }

  public static EntityData particleColor(Color color) {
    return particleColor(color.getRGB());
  }
}
