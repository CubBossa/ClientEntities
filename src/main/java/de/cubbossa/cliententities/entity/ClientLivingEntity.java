package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHurtAnimation;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.TrackedEntityEquipment;
import de.cubbossa.cliententities.TrackedField;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ClientLivingEntity extends ClientDamageable implements LivingEntity {

  TrackedEntityEquipment equipment = new TrackedEntityEquipment(this);
  TrackedField<@Nullable Location> bedLocation = new TrackedField<>();
  TrackedField<@Nullable Color> potionEffectColor = new TrackedField<>();
  TrackedBoolField potionEffectAmbient = new TrackedBoolField();
  TrackedField<Integer> arrowsInBody = new TrackedField<>(0);
  TrackedField<Integer> beeStingersInBody = new TrackedField<>(0);
  TrackedBoolField isHandActive = new TrackedBoolField();
  TrackedBoolField activeHandMainHand = new TrackedBoolField(true);
  @Nullable Entity leashHolder = null;

  public ClientLivingEntity(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  @Override
  public int getItemInUseTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  public ItemStack getItemInUse() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public double getEyeHeight() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public double getEyeHeight(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Location getEyeLocation() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public List<Block> getLineOfSight(@Nullable Set<Material> set, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Block getTargetBlock(@Nullable Set<Material> set, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public List<Block> getLastTwoTargetBlocks(@Nullable Set<Material> set, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public Block getTargetBlockExact(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public Block getTargetBlockExact(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public RayTraceResult rayTraceBlocks(double v) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public RayTraceResult rayTraceBlocks(double v, @NotNull FluidCollisionMode fluidCollisionMode) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getRemainingAir() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setRemainingAir(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setRiptiding(boolean b) {
    pose.setValue(Pose.SPIN_ATTACK);
  }

  @Override  @Deprecated
  public int getMaximumAir() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setMaximumAir(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getArrowCooldown() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setArrowCooldown(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public int getArrowsInBody() {
    return arrowsInBody.getValue();
  }

  public void setArrowsInBody(int count) {
    setMeta(this.arrowsInBody, count);
  }

  @Override  @Deprecated
  public int getMaximumNoDamageTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setMaximumNoDamageTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public double getLastDamage() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setLastDamage(double v) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getNoDamageTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setNoDamageTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getNoActionTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setNoActionTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public Player getKiller() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean addPotionEffect(@NotNull PotionEffect potionEffect) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean addPotionEffect(@NotNull PotionEffect potionEffect, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean addPotionEffects(@NotNull Collection<PotionEffect> collection) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean hasPotionEffect(@NotNull PotionEffectType potionEffectType) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public PotionEffect getPotionEffect(@NotNull PotionEffectType potionEffectType) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void removePotionEffect(@NotNull PotionEffectType potionEffectType) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Collection<PotionEffect> getActivePotionEffects() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean hasLineOfSight(@NotNull Entity entity) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean getRemoveWhenFarAway() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setRemoveWhenFarAway(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  public EntityEquipment getEquipment() {
    return equipment;
  }

  @Override  @Deprecated
  public void setCanPickupItems(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean getCanPickupItems() {
    throw new ServerSideMethodNotSupported();
  }

  public void setBeeStingersInBody(int count) {
    setMeta(this.beeStingersInBody, count);
  }

  public void setPotionEffectColor(@Nullable Color color) {
    setMeta(this.potionEffectColor, color);
  }

  public void setPotionEffectAmbient(boolean ambient) {
    setMeta(this.potionEffectAmbient, ambient);
  }

  public void setPotionEffect(@Nullable PotionEffect effect) {
    setPotionEffectColor(effect == null ? null : effect.getType().getColor());
    setPotionEffectAmbient(effect != null && effect.isAmbient());
  }

  public boolean isLeashed() {
    return leashHolder != null;
  }

  @NotNull
  @Override
  public Entity getLeashHolder() throws IllegalStateException {
    if (leashHolder == null) {
      throw new IllegalStateException();
    }
    return leashHolder;
  }

  public boolean setLeashHolder(@Nullable Entity holder) {
    leashHolder = holder;
    statelessEffect.add(() -> PacketInfo.packet(
        new WrapperPlayServerAttachEntity(entityId, holder == null ? -1 : holder.getEntityId(), true)
    ));
    return true;
  }

  public boolean isGliding() {
    return elytraFlying.getBooleanValue();
  }

  public void setGliding(boolean gliding) {
    setMeta(this.elytraFlying, gliding);
  }

  @Override
  public boolean isSwimming() {
    return pose.getValue() == Pose.SWIMMING;
  }

  @Override
  public void setSwimming(boolean swimming) {
    setMeta(pose, Pose.SWIMMING);
  }

  public boolean isRiptiding() {
    return pose.getValue() == Pose.SPIN_ATTACK;
  }

  public boolean isSleeping() {
    return pose.getValue() == Pose.SLEEPING;
  }

  @Override  @Deprecated
  public boolean isClimbing() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setAI(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean hasAI() {
    return false;
  }

  @Override  @Deprecated
  public void attack(@NotNull Entity entity) {
    throw new ServerSideMethodNotSupported();
  }

  public boolean sleep(@NotNull Location location) {
    teleport(location);
    pose.setValue(Pose.SLEEPING);
    bedLocation.setValue(location);
    return true;
  }

  public void wakeup() {
    if (pose.getValue() == Pose.SLEEPING) {
      pose.setValue(Pose.STANDING);
    }
  }

  public @Nullable Location getBedLocation() {
    return bedLocation.getValue();
  }

  @Override
  public void setItemInUseTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  public void swingMainHand() {
    playAnimation(Animation.SWING_MAIN_ARM);
  }

  public void swingOffHand() {
    playAnimation(Animation.SWING_OFFHAND);
  }

  public void playHurtAnimation(float v) {
    statelessEffect.add(() -> PacketInfo.packet(new WrapperPlayServerHurtAnimation(entityId, v)));
  }

  @Override  @Deprecated
  public void setCollidable(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean isCollidable() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Set<UUID> getCollidableExemptions() {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public <T> T getMemory(@NotNull MemoryKey<T> memoryKey) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public <T> void setMemory(@NotNull MemoryKey<T> memoryKey, @Nullable T t) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public Sound getHurtSound() {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override  @Deprecated
  public Sound getDeathSound() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Sound getFallDamageSound(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Sound getFallDamageSoundSmall() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Sound getFallDamageSoundBig() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Sound getDrinkingSound(@NotNull ItemStack itemStack) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override  @Deprecated
  public Sound getEatingSound(@NotNull ItemStack itemStack) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean canBreatheUnderwater() {
    throw new ServerSideMethodNotSupported();
  }

  public void setInvisible(boolean invisible) {
    super.invisible.setValue(invisible);
  }

  public boolean isInvisible() {
    return invisible.getBooleanValue();
  }

  @NotNull
  public <T extends Projectile> T launchProjectile(@NotNull Class<? extends T> projectile) {
    return null; //TODO
  }

  @NotNull
  public <T extends Projectile> T launchProjectile(@NotNull Class<? extends T> projectile, @Nullable Vector velocity) {
    return null; //TODO
  }

  @Override
  public List<UpdateInfo> state(boolean onlyIfChanged) {
    List<UpdateInfo> info = super.state(onlyIfChanged);

    if (equipment.hasChanged()) {

      List<Equipment> equip = new ArrayList<>();
      equip.add(new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(equipment.getBoots())));
      equip.add(new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(equipment.getLeggings())));
      equip.add(new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(equipment.getChestplate())));
      equip.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(equipment.getHelmet())));
      equip.add(new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(equipment.getItemInMainHand())));
      equip.add(new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(equipment.getItemInOffHand())));

      info.add(PacketInfo.packet(new WrapperPlayServerEntityEquipment(entityId, equip)));
      equipment.setChanged(true);
    }
    return info;
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (isHandActive.hasChanged()) {
      data.add(new EntityData(8, EntityDataTypes.BYTE, (byte) (
          0x1 | (activeHandMainHand.getBooleanValue() ? 0 : 0x2) | (isRiptiding() ? 0x4 : 0)
          )));
    }
    if (health.hasChanged()) {
      data.add(new EntityData(9, EntityDataTypes.FLOAT, health.getValue().floatValue()));
    }
    if (potionEffectColor.hasChanged()) {
      data.add(new EntityData(10, EntityDataTypes.INT, potionEffectColor.getValue() == null
          ? 0 : potionEffectColor.getValue().asRGB()));
    }
    if (potionEffectAmbient.hasChanged()) {
      data.add(new EntityData(11, EntityDataTypes.BOOLEAN, potionEffectAmbient.getBooleanValue()));
    }
    if (arrowsInBody.hasChanged()) {
      data.add(new EntityData(12, EntityDataTypes.INT, arrowsInBody.getValue()));
    }
    if (beeStingersInBody.hasChanged()) {
      data.add(new EntityData(13, EntityDataTypes.INT, beeStingersInBody.getValue()));
    }
    if (bedLocation.hasChanged()) {
      data.add(new EntityData(14, EntityDataTypes.OPTIONAL_BLOCK_POSITION, Optional.ofNullable(bedLocation.getValue() == null
          ? null : SpigotConversionUtil.fromBukkitLocation(bedLocation.getValue()))));
    }
    return data;
  }

  @Nullable
  @Override  @Deprecated
  public AttributeInstance getAttribute(@NotNull Attribute attribute) {
    throw new ServerSideMethodNotSupported();
  }
}
