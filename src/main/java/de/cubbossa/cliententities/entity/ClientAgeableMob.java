package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.entity.EntityType;

import java.util.List;

public abstract class ClientAgeableMob extends ClientMob {

    private boolean baby = false;

    public ClientAgeableMob(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
        super(playerSpace, entityId, entityType);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (baby) {
            data.add(new EntityData(16, EntityDataTypes.BOOLEAN, true));
        }
        return data;
    }

    public void setBaby() {
        baby = setMeta(this.baby, true);
    }

    public void setAdult() {
        baby = setMeta(this.baby, false);
    }

    public boolean isAdult() {
        return !baby;
    }
}
