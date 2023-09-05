package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import de.cubbossa.cliententities.ClientEntityEquipment;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@Setter
public abstract class ClientLivingEntity extends ClientDamageable {

  EntityEquipment equipment = new ClientEntityEquipment(this);
  @Nullable Location bedLocation = null;
  @Nullable Color potionEffectColor = null;
  boolean potionEffectAmbient = false;
  int arrowsInBody = 0;
  int beeStingersInBody = 0;
  boolean isHandActive = false;
  boolean activeHandMainHand = true;

  // change list
  boolean equipmentChanged = false;
  @Nullable Entity leashHolder = null;

  public ClientLivingEntity(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  public void setArrowsInBody(int count) {
    this.arrowsInBody = setMeta(this.arrowsInBody, count);
  }

  public void setBeeStingersInBody(int count) {
    this.beeStingersInBody = setMeta(this.beeStingersInBody, count);
  }

  public void setPotionEffectColor(@Nullable Color color) {
    this.potionEffectColor = setMeta(this.potionEffectColor, color);
  }

  public void setPotionEffectAmbient(boolean ambient) {
    this.potionEffectAmbient = setMeta(this.potionEffectAmbient, ambient);
  }

  public void setPotionEffect(@Nullable PotionEffect effect) {
    setPotionEffectColor(effect == null ? null : effect.getType().getColor());
    setPotionEffectAmbient(effect != null && effect.isAmbient());
  }

  public boolean isLeashed() {
    return leashHolder != null;
  }

  public boolean setLeashHolder(@Nullable Entity holder) {
    leashHolder = holder;
    runnableEffects.add(player -> {
      PacketEvents.getAPI().getPlayerManager().sendPacket(player,
          new WrapperPlayServerAttachEntity(entityId, holder == null ? -1 : holder.getEntityId(), true)
      );
    });
    return true;
  }

  public boolean isGliding() {
    return isElytraFlying();
  }

  public void setGliding(boolean gliding) {
    this.elytraFlying = setMeta(this.elytraFlying, gliding);
  }

  @Override
  public boolean isSwimming() {
    return pose == Pose.SWIMMING;
  }

  @Override
  public void setSwimming(boolean swimming) {
    pose = setMeta(pose, Pose.SWIMMING);
  }

  public boolean isRiptiding() {
    return pose == Pose.SPIN_ATTACK;
  }

  public boolean isSleeping() {
    return pose == Pose.SLEEPING;
  }

  public boolean sleep(@NotNull Location location) {
    teleport(location);
    setPose(Pose.SLEEPING);
    bedLocation = location;
    return true;
  }

  public void wakeup() {
    if (pose == Pose.SLEEPING) {
      setPose(Pose.STANDING);
    }
  }

  public @Nullable Location getBedLocation() {
    return bedLocation;
  }

  public void swingMainHand() {
    playAnimation(Animation.SWING_MAIN_ARM);
  }

  public void swingOffHand() {
    playAnimation(Animation.SWING_OFFHAND);
  }

  public void playHurtAnimation(float v) {
    runnableEffects.add(player -> {
      //PacketEvents.getAPI().getPlayerManager().sendPacket(player,
      //    new WrapperPlayServerHurtAnimation(v));
    });
  }

  @NotNull
  public abstract EntityCategory getCategory();

  public void setInvisible(boolean invisible) {
    super.setVisible(!invisible);
  }

  public boolean isInvisible() {
    return !visible;
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
  public void announce(Collection<Player> viewers) {
    super.announce(viewers);
    PacketEventsAPI<?> api = PacketEvents.getAPI();
    for (Player player : viewers) {
      if (equipmentChanged) {

        List<Equipment> equip = new ArrayList<>();
        equip.add(new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(equipment.getBoots())));
        equip.add(new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(equipment.getLeggings())));
        equip.add(new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(equipment.getChestplate())));
        equip.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(equipment.getHelmet())));
        equip.add(new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(equipment.getItemInMainHand())));
        equip.add(new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(equipment.getItemInOffHand())));

        api.getPlayerManager().sendPacket(player, new WrapperPlayServerEntityEquipment(entityId, equip));
        equipmentChanged = false;
      }
    }
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (isHandActive) {
      data.add(new EntityData(8, EntityDataTypes.BYTE, (byte) (
          0x1 | (activeHandMainHand ? 0 : 0x2) | (isRiptiding() ? 0x4 : 0)
          )));
    }
    if (getHealth() != 1) {
      data.add(new EntityData(9, EntityDataTypes.FLOAT, (float) getHealth()));
    }
    if (potionEffectColor != null) {
      data.add(new EntityData(10, EntityDataTypes.INT, potionEffectColor.asRGB()));
    }
    if (potionEffectAmbient) {
      data.add(new EntityData(11, EntityDataTypes.BOOLEAN, true));
    }
    if (arrowsInBody != 0) {
      data.add(new EntityData(12, EntityDataTypes.INT, arrowsInBody));
    }
    if (beeStingersInBody != 0) {
      data.add(new EntityData(13, EntityDataTypes.INT, beeStingersInBody));
    }
    if (bedLocation != null) {
      data.add(new EntityData(14, EntityDataTypes.OPTIONAL_BLOCK_POSITION, SpigotConversionUtil.fromBukkitLocation(bedLocation)));
    }
    return data;
  }
}
