package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class ItemDisplayDataWrapper extends DisplayDataWrapper {

  protected ItemDisplayDataWrapper() {}

  public static EntityData item(com.github.retrooper.packetevents.protocol.item.ItemStack stack) {
    return new AbstractEntityDataWrapper(EntityDataTypes.ITEMSTACK, stack) {

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

  public static EntityData displayType(ItemDisplayType type) {
    return displayType((byte) type.ordinal());
  }

  public static EntityData displayType(byte index) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, index) {

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

  public enum ItemDisplayType {
    NONE,
    THIRD_PERSON_LEFT_HAND,
    THIRD_PERSON_RIGHT_HAND,
    FIRST_PERSON_LEFT_HAND,
    FIRST_PERSON_RIGHT_HAND,
    HEAD,
    GUI,
    GROUND,
    FIXED
  }
}
