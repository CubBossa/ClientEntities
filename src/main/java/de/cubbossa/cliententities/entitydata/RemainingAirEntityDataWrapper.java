package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class RemainingAirEntityDataWrapper extends EntityDataWrapper {


    public RemainingAirEntityDataWrapper(int ticks) {
        super(EntityDataTypes.INT, ticks);
        setIndex(versionedIndex());
    }

    @Override
    public Integer getValue() {
        return (Integer) super.getValue();
    }
    @Override
    protected int versionedIndex() {
        return 1; // in this case never changed from 1.8 to 1.20.2
    }
}
