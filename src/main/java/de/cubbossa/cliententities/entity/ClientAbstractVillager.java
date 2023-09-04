package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpace;
import org.bukkit.entity.EntityType;

import java.util.List;

public abstract class ClientAbstractVillager extends ClientAgeableMob {

    int headShakeTimer = 40;

    public ClientAbstractVillager(PlayerSpace playerSpace, int entityId, EntityType entityType) {
        super(playerSpace, entityId, entityType);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (headShakeTimer != 40) {
            data.add(new EntityData(17, EntityDataTypes.INT, headShakeTimer));
        }
        return data;
    }
}
