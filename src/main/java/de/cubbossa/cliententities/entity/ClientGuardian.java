package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedField;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ClientGuardian extends ClientMob implements Guardian {

  TrackedField<Boolean> retractingSpikes = new TrackedField<>(false);
  TrackedField<Boolean> showLaser = new TrackedField<>(false);
  TrackedField<Integer> targetEntity = new TrackedField<>();

  public ClientGuardian(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.GUARDIAN);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (retractingSpikes.hasChanged()) {
      data.add(new EntityData(16, EntityDataTypes.BOOLEAN, Boolean.TRUE.equals(retractingSpikes.getValue())));
    }
    if (targetEntity.hasChanged() || showLaser.hasChanged()) {
      data.add(new EntityData(17, EntityDataTypes.INT, targetEntity.getValue() == null ? 0 : targetEntity.getValue()));
    }
    return data;
  }

  @Override
  public @NotNull EntityCategory getCategory() {
    return EntityCategory.WATER;
  }

  @Override
  public boolean setLaser(boolean b) {
    showLaser.setValue(true);
    return true;
  }

  @Override
  public boolean hasLaser() {
    return Boolean.TRUE.equals(showLaser.getValue()) && getTarget() != null;
  }

  @Override
  public int getLaserDuration() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setLaserTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public int getLaserTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean isElder() {
    return false;
  }

  @Override
  public void setElder(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean isMoving() {
    throw new ServerSideMethodNotSupported();
  }
}
