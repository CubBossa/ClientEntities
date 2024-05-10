package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedBoolField;
import org.bukkit.entity.Ageable;

import java.util.List;

public abstract class ClientAgeableMob extends ClientMob implements Ageable {

    TrackedBoolField baby = new TrackedBoolField(false);

    public ClientAgeableMob(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
        super(playerSpace, entityId, entityType);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (baby.hasChanged()) {
            data.add(new EntityData(16, EntityDataTypes.BOOLEAN, baby.getBooleanValue()));
            baby.flushChanged();
        }
        return data;
    }

    @Override
    public int getAge() {
        throw new ServerSideMethodNotSupported();
    }

    @Override
    public void setAge(int i) {
        throw new ServerSideMethodNotSupported();
    }

    @Override
    public void setAgeLock(boolean b) {
        throw new ServerSideMethodNotSupported();
    }

    @Override
    public boolean getAgeLock() {
        throw new ServerSideMethodNotSupported();
    }

    public void setBaby() {
    setMeta(this.baby, true);
    }

    public void setAdult() {
    setMeta(this.baby, false);
    }

    public boolean isAdult() {
        return !baby.getBooleanValue();
    }

    @Override
    public boolean canBreed() {
        throw new ServerSideMethodNotSupported();
    }

    @Override
    public void setBreed(boolean b) {
        throw new ServerSideMethodNotSupported();
    }
}
