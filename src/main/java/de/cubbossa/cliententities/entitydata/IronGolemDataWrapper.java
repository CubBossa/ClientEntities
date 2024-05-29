package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import javax.swing.text.html.parser.Entity;

public class IronGolemDataWrapper extends EntityDataWrapper {

  public static EntityData playerCreated(boolean value) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, value ? 1 : 0) {
      @Override
      protected int versionedIndex() {
        return 16;
      }
    };
  }
}
