package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedBoolField;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ClientMob extends ClientLivingEntity implements Mob {

  TrackedBoolField leftHanded = new TrackedBoolField();

  public ClientMob(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (leftHanded.hasChanged()) {
      data.add(new EntityData(15, EntityDataTypes.BYTE, (byte) (
          0x1 | (leftHanded.getBooleanValue() ? 0x2 : 0)
      )));
    }
    return data;
  }

  public void setLeftHanded(boolean leftHanded) {
    setMeta(this.leftHanded, leftHanded);
  }

  @Override
  public void setTarget(@Nullable LivingEntity livingEntity) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  public LivingEntity getTarget() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setAware(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean isAware() {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  public Sound getAmbientSound() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setLootTable(@Nullable LootTable lootTable) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  public LootTable getLootTable() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setSeed(long l) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public long getSeed() {
    throw new ServerSideMethodNotSupported();
  }
}
