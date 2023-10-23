package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public abstract class AbstractEntityDataWrapper extends EntityData {

    final ServerVersion serverVersion;

    protected AbstractEntityDataWrapper(EntityDataType<?> type, Object value) {
        super(-1, type, value);
        this.serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
        setIndex(versionedIndex());
    }

    /**
     * @return -1 if this field (not the index) does not exist on the current version. Otherwise, the index of the field
     * will be returned according to the current server version.
     */
    protected abstract int versionedIndex();


}
