package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import org.bukkit.Sound;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ClientMob extends ClientLivingEntity {

    private boolean leftHanded = false;

    public ClientMob(PlayerSpace playerSpace, int entityId, EntityType entityType) {
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
