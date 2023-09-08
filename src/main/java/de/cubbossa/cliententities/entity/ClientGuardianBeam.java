package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClientGuardianBeam extends ClientGuardian {

  TrackedField<@Nullable Integer> targetEntityId = new TrackedField<>();
  @Nullable ClientBlockDisplay artificialTarget = null;

  public ClientGuardianBeam(PlayerSpaceImpl playerSpace, int entityId, Entity target) {
    super(playerSpace, entityId);
    invisible.setValue(false);
    setTargetEntity(target);
  }
  public ClientGuardianBeam(PlayerSpaceImpl playerSpace, int entityId, Location target) {
    super(playerSpace, entityId);
    invisible.setValue(false);
    setTargetLocation(target);
  }

  public void setStartLocation(Location location) {
    this.teleport(location);
  }

  public void setTargetLocation(Location location) {
    if (artificialTarget == null) {
      artificialTarget = playerSpace.spawn(location, BlockDisplay.class);
      artificialTarget.setViewRange(0);
    } else {
      artificialTarget.teleport(location);
    }
    if (!Objects.equals(targetEntityId.getValue(), artificialTarget.getEntityId())) {
      setMeta(targetEntityId, artificialTarget.getEntityId());
    }
  }

  public void setTargetEntity(@Nullable Entity entity) {
    if (artificialTarget != null) {
      artificialTarget.remove();
    }
    setMeta(targetEntityId, entity == null ? null : entity.getEntityId());
  }
}
