package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;

public class ThrownItemProjectileDataWrapper extends EntityDataWrapper {

  protected ThrownItemProjectileDataWrapper() {}

  public static EntityData item(ItemStack itemStack) {
    return new AbstractEntityDataWrapper(EntityDataTypes.ITEMSTACK, itemStack) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
          return 8;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
          return 7;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_11)) {
          return 6;
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
          return 5;
        }
        return -1;
      }
    };
  }
}
