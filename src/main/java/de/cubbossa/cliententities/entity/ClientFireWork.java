package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.TrackedField;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ClientFireWork extends ClientEntity implements Firework {

  TrackedField<ItemStack> fireWorkInfo = new TrackedField<>();
  TrackedField<@Nullable LivingEntity> attachedTo = new TrackedField<>();
  TrackedBoolField shotAtAngle = new TrackedBoolField();
  boolean detonated = false;

  public ClientFireWork(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.FIREWORK_ROCKET);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (fireWorkInfo != null) {
      data.add(new EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(fireWorkInfo.getValue())));
    }
    data.add(new EntityData(9, EntityDataTypes.OPTIONAL_INT, Optional.ofNullable(attachedTo.getValue() == null ? null : attachedTo.getValue().getEntityId())));
    if (shotAtAngle.getBooleanValue()) {
      data.add(new EntityData(10, EntityDataTypes.BOOLEAN, true));
    }
    return data;
  }

  @NotNull
  public FireworkMeta getFireworkMeta() {
    if (fireWorkInfo.getValue() == null) {
      fireWorkInfo.setValue(new ItemStack(Material.FIREWORK_ROCKET));
    }
    if (fireWorkInfo.getValue().getType() != Material.FIREWORK_ROCKET) {
      return (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
    }
    FireworkMeta meta = (FireworkMeta) fireWorkInfo.getValue().getItemMeta();
    if (meta == null) {
      meta = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
    }
    return meta.clone();
  }

  public void setFireworkMeta(@NotNull FireworkMeta fireworkMeta) {
    if (fireWorkInfo.getValue() == null) {
      fireWorkInfo.setValue(new ItemStack(Material.FIREWORK_ROCKET));
    }
    if (fireWorkInfo.getValue().getType() != Material.FIREWORK_ROCKET) {
      fireWorkInfo.getValue().setType(Material.FIREWORK_ROCKET);
    }
    if (!this.fireWorkInfo.getValue().setItemMeta(fireworkMeta)) {
      throw new IllegalStateException("Could not set firework meta");
    }
  }

  public boolean setAttachedTo(@Nullable LivingEntity livingEntity) {
    setMeta(this.attachedTo, livingEntity);
    return true;
  }

  @Nullable
  @Override
  public LivingEntity getAttachedTo() {
    return null;
  }

  @Override  @Deprecated
  public boolean setLife(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getLife() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean setMaxLife(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getMaxLife() {
    throw new ServerSideMethodNotSupported();
  }

  public void detonate() {
    playEffect(EntityEffect.FIREWORK_EXPLODE);
    detonated = true;
  }

  @Override
  public boolean isDetonated() {
    return false;
  }

  @Override
  public boolean isShotAtAngle() {
    return false;
  }

  public void setShotAtAngle(boolean b) {
    setMeta(this.shotAtAngle, b);
  }

  @Nullable
  @Override  @Deprecated
  public ProjectileSource getShooter() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setShooter(@Nullable ProjectileSource projectileSource) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean doesBounce() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setBounce(boolean b) {

  }
}
