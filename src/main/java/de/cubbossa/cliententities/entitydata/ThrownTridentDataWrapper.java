package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import java.util.Map;

public class ThrownTridentDataWrapper extends AbstractArrowDataWrapper {

  public static EntityData loyaltyLevel(int level) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, level) {
      @Override
      protected int versionedIndex() {
        return index(this.serverVersion, Map.of(
        ServerVersion.V_1_13, 8,
        ServerVersion.V_1_14, 10,
        ServerVersion.V_1_15, 9,
        ServerVersion.V_1_17, 10
        ));
      }
    };
  }

  public static EntityData setEnchantmentGlint(boolean glint) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BOOLEAN, glint) {
      @Override
      protected int versionedIndex() {
        return index(this.serverVersion, Map.of(
        ServerVersion.V_1_15, 10,
        ServerVersion.V_1_17, 11
        ));
      }
    };
  }
}
