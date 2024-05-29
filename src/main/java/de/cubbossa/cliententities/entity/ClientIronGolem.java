package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.entitydata.IronGolemDataWrapper;
import java.util.List;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.IronGolem;
import org.jetbrains.annotations.NotNull;

public class ClientIronGolem extends ClientGolem implements IronGolem {

  TrackedBoolField playerMade = new TrackedBoolField(false);

  public ClientIronGolem(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  @Override
  public boolean isPlayerCreated() {
    return playerMade.getValue();
  }

  @Override
  public void setPlayerCreated(boolean b) {
    setMeta(playerMade, b);
  }

  @NotNull
  @Override
  public EntityCategory getCategory() {
    return EntityCategory.NONE;
  }

  @Override
  List<EntityData> metaData() {
    var meta = super.metaData();
    if (playerMade.hasChanged()) {
      meta.add(IronGolemDataWrapper.playerCreated(playerMade.getBooleanValue()));
    }
    return meta;
  }
}
