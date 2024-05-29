package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class SlimeDataWrapper extends EntityDataWrapper {

  public static EntityData size(int size) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, size) {

      @Override
      protected int versionedIndex() {
        return 16;
      }
    };
  }

}
