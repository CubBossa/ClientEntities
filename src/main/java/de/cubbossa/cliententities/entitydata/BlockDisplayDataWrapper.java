package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public class BlockDisplayDataWrapper {

  protected BlockDisplayDataWrapper() {
  }

  public static EntityData block(WrappedBlockState blockState) {
    return block(blockState.getGlobalId());
  }

  public static EntityData block(int id) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BLOCK_STATE, id) {

      @Override
      protected int versionedIndex() {
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
          return -1;
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
          return 23;
        }
        return 22;
      }
    };
  }
}
