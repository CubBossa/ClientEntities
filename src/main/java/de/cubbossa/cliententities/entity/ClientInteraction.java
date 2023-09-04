package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class ClientInteraction extends ClientEntity {

  float interactionWidth = 1;
  float interactionHeight = 1;
  boolean responsive = true;

  public ClientInteraction(PlayerSpace playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.INTERACTION);
  }

  public void setInteractionWidth(float width) {
    this.interactionWidth = setMeta(this.interactionWidth, width);
  }

  public void setInteractionHeight(float height) {
    this.interactionHeight = setMeta(this.interactionHeight, height);
  }

  public void setResponsive(boolean response) {
    this.responsive = setMeta(this.responsive, response);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    data.add(new EntityData(8, EntityDataTypes.FLOAT, interactionWidth));
    data.add(new EntityData(9, EntityDataTypes.FLOAT, interactionHeight));
    data.add(new EntityData(10, EntityDataTypes.BOOLEAN, responsive));
    return data;
  }
}
