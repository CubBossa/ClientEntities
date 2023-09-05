package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.entity.EntityType;

import java.util.List;

public abstract class ClientMob extends ClientLivingEntity {

    private boolean leftHanded = false;

    public ClientMob(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
        super(playerSpace, entityId, entityType);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (leftHanded) {
            data.add(new EntityData(15, EntityDataTypes.BYTE, (byte) (
                    0x1 | 0x2
            )));
        }
        return data;
    }

    public void setLeftHanded(boolean leftHanded) {
        this.leftHanded = setMeta(this.leftHanded, leftHanded);
    }
}
