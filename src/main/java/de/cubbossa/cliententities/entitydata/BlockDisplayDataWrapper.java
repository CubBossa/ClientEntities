package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public class BlockDisplayDataWrapper {

    protected BlockDisplayDataWrapper() {}

    public static class Block extends AbstractEntityDataWrapper {
        public Block(int id) {
            super(EntityDataTypes.BLOCK_STATE, id);
        }

        public Block(WrappedBlockState blockState) {
            this(blockState.getGlobalId());
        }

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
    }
}
