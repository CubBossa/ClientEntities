package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import de.cubbossa.cliententities.UntickedEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@Setter

public class ClientEntity implements Entity, UntickedEntity {

  static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

  final int entityId;
  final UUID uniqueId;
  final EntityType type;
  final PlayerSpace playerSpace;

  boolean alive = true;
  Location location = new Location(null, 0, 0, 0);
  Vector velocity = new Vector(0, 0, 0);
  double height = 0;
  double width = 0;
  boolean onFire = false;
  boolean crouching = false;
  boolean sprinting = false;
  boolean swimming = false;
  boolean visible = true;
  boolean glowing = false;
  boolean flying = false;
  int airTicks = 300;
  boolean silent = true;
  boolean gravity = true;
  Pose pose = Pose.STANDING;
  int frozenTicks = 0;

  boolean invulnerable = true;

  boolean customNameVisible = false;
  Component customName;

  // changed properties
  boolean aliveChanged = false;
  boolean locationChanged = false;
  boolean velocityChanged = false;
  boolean metaChanged = false;
  List<Byte> statusEffects = new LinkedList<>();

  public ClientEntity(PlayerSpace playerSpace, int entityId, EntityType entityType) {
    this.playerSpace = playerSpace;
    this.entityId = entityId;
    this.uniqueId = UUID.randomUUID();
    this.type = entityType;

    // entity not yet spawned
    this.aliveChanged = true;
  }

  @Override
  public void update(Collection<Player> viewers) {
    for (Player player : viewers) {
      PacketEventsAPI<?> api = PacketEvents.getAPI();

      // Spawn entity
      if (aliveChanged && alive) {
        api.getPlayerManager().sendPacket(player,
            new WrapperPlayServerSpawnEntity(entityId, Optional.ofNullable(uniqueId), SpigotConversionUtil.fromBukkitEntityType(type),
                new Vector3d(location.getX(), location.getY(), location.getZ()), 0, 0, 0, data(),
                Optional.ofNullable(velocity == null ? null : new Vector3d(velocity.getX(), velocity.getY(), velocity.getZ()))
            )
        );
        aliveChanged = false;
        locationChanged = false;
      }
      // Play Status Effects on Entity - before delete, for example rocket detonation.
      if (!statusEffects.isEmpty()) {
        statusEffects.forEach(id -> api.getPlayerManager().sendPacket(player, new WrapperPlayServerEntityStatus(entityId, id)));
        statusEffects.clear();
      }
      // Delete Entity
      if (aliveChanged && !alive) {
        api.getPlayerManager().sendPacket(player,
            new WrapperPlayServerDestroyEntities(entityId)
        );
        aliveChanged = false;
        playerSpace.releaseEntity(this);
      }
      // Change location
      if (locationChanged && location != null) {
        api.getPlayerManager().sendPacket(player,
            new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), true)
        );
        locationChanged = false;
      }
      // change velocity
      if (velocityChanged && velocity != null) {
        api.getPlayerManager().sendPacket(player,
            new WrapperPlayServerEntityVelocity(entityId, new Vector3d(velocity.getX(), velocity.getY(), velocity.getZ())));
        velocityChanged = false;
      }
      // Change Meta Data
      if (metaChanged) {
        try {
          List<EntityData> data = metaData();
          api.getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
        } catch (Throwable t) {
          t.printStackTrace();
        }
        metaChanged = false;
      }
    }
  }

  int data() {
    return 0;
  }

  List<EntityData> metaData() {
    List<EntityData> data = new ArrayList<>();
    // flags
    byte mask = (byte) (
        (onFire ? 0x01 : 0)
            | (crouching ? 0x02 : 0)
            | (sprinting ? 0x08 : 0)
            | (swimming ? 0x10 : 0)
            | (!visible ? 0x20 : 0)
            | (glowing ? 0x40 : 0)
            | (flying ? 0x80 : 0)
    );
    if (mask != 0) {
      data.add(new EntityData(0, EntityDataTypes.BYTE, mask));
    }
    if (airTicks != 300) {
      data.add(new EntityData(1, EntityDataTypes.INT, airTicks));
    }
    // data.add(new EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT, Optional.ofNullable(getCustomName())));
    if (customNameVisible) {
      data.add(new EntityData(3, EntityDataTypes.BOOLEAN, true));
    }
    if (silent) {
      data.add(new EntityData(4, EntityDataTypes.BOOLEAN, true));
    }
    if (!gravity) {
      data.add(new EntityData(5, EntityDataTypes.BOOLEAN, false));
    }
    if (pose != Pose.STANDING) {
      data.add(new EntityData(6, EntityDataTypes.ENTITY_POSE, pose));
    }
    if (frozenTicks != 0) {
      data.add(new EntityData(7, EntityDataTypes.INT, frozenTicks));
    }
    return data;
  }

  protected <T> T setMeta(T old, T val) {
    if (Objects.equals(old, val)) {
      return val;
    }
    metaChanged = true;
    return val;
  }

  public void setCustomName(Component customName) {
    this.customName = customName;
  }

  public void setCustomName(String customName) {
    this.customName = Component.text(customName);
  }

  public String getCustomName() {
    return GSON.serialize(customName);
  }

  @NotNull
  @Override
  public Location getLocation() {
    return location;
  }

  @Nullable
  @Override
  public Location getLocation(@Nullable Location loc) {
    if (loc == null) {
      return null;
    }
    loc.setX(location.getX());
    loc.setY(location.getY());
    loc.setZ(location.getZ());
    return loc;
  }

  @NotNull
  @Override
  public BoundingBox getBoundingBox() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isOnGround() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isInWater() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public World getWorld() {
    return getLocation().getWorld();
  }

  @Override
  public void setRotation(float yaw, float pitch) {
    this.location.setYaw(yaw);
    this.location.setPitch(pitch);
    this.locationChanged = true;
  }

  @Override
  public boolean teleport(@NotNull Location location) {
    this.location = location.clone();
    this.locationChanged = true;
    return true;
  }

  @Override
  public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
    this.location = location.clone();
    this.locationChanged = true;
    return true;
  }

  @Override
  public boolean teleport(@NotNull Entity destination) {
    this.location = destination.getLocation().clone();
    this.locationChanged = true;
    return true;
  }

  @Override
  public boolean teleport(@NotNull Entity destination, @NotNull PlayerTeleportEvent.TeleportCause cause) {
    this.location = destination.getLocation().clone();
    this.locationChanged = true;
    return true;
  }

  public void setVelocity(Vector velocity) {
    this.velocity = velocity;
    velocityChanged = true;
  }

  @NotNull
  @Override
  public List<Entity> getNearbyEntities(double x, double y, double z) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public int getFireTicks() {
    return onFire ? 1 : 0;
  }

  @Override
  public int getMaxFireTicks() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setFireTicks(int ticks) {
    this.onFire = setMeta(this.onFire, ticks > 0);
  }

  @Override
  public void setVisualFire(boolean fire) {
    this.onFire = setMeta(this.onFire, fire);
  }

  @Override
  public boolean isVisualFire() {
    return onFire;
  }

  @Override
  public int getFreezeTicks() {
    return frozenTicks;
  }

  @Override
  public int getMaxFreezeTicks() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setFreezeTicks(int ticks) {
    this.frozenTicks = setMeta(this.frozenTicks, ticks);
  }

  @Override
  public boolean isFrozen() {
    return frozenTicks > 0;
  }

  @Override
  public void remove() {
    alive = false;
    aliveChanged = true;
  }

  @Override
  public boolean isDead() {
    return !alive;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void sendMessage(@NotNull String message) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void sendMessage(@NotNull String... messages) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void sendMessage(@Nullable UUID sender, @NotNull String message) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public Server getServer() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public String getName() {
    return customName == null ? getClass().getName() : GSON.serialize(customName);
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  public void setPersistent(boolean persistent) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public Entity getPassenger() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean setPassenger(@NotNull Entity passenger) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public List<Entity> getPassengers() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean addPassenger(@NotNull Entity passenger) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean removePassenger(@NotNull Entity passenger) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean eject() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public float getFallDistance() {
    return 0;
  }

  @Override
  public void setFallDistance(float distance) {

  }

  @Override
  public void setLastDamageCause(@Nullable EntityDamageEvent event) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public EntityDamageEvent getLastDamageCause() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public int getTicksLived() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setTicksLived(int value) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void playEffect(@NotNull EntityEffect type) {
    statusEffects.add(type.getData());
  }

  @NotNull
  @Override
  public Sound getSwimSound() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public Sound getSwimSplashSound() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public Sound getSwimHighSpeedSplashSound() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isInsideVehicle() {
    return false;
  }

  @Override
  public boolean leaveVehicle() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public Entity getVehicle() {
    return null;
  }

  @Override
  public void setCustomNameVisible(boolean flag) {
    this.setMeta(this.customNameVisible, flag);
  }

  @Override
  public boolean isCustomNameVisible() {
    return customNameVisible;
  }

  @Override
  public void setVisibleByDefault(boolean visible) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isVisibleByDefault() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public int getPortalCooldown() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setPortalCooldown(int cooldown) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public Set<String> getScoreboardTags() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean addScoreboardTag(@NotNull String tag) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean removeScoreboardTag(@NotNull String tag) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public PistonMoveReaction getPistonMoveReaction() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public BlockFace getFacing() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public Pose getPose() {
    return pose;
  }

  @NotNull
  @Override
  public SpawnCategory getSpawnCategory() {
    return SpawnCategory.MISC;
  }

  @NotNull
  @Override
  public Spigot spigot() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean hasMetadata(@NotNull String metadataKey) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isPermissionSet(@NotNull String name) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isPermissionSet(@NotNull Permission perm) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean hasPermission(@NotNull String name) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean hasPermission(@NotNull Permission perm) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void removeAttachment(@NotNull PermissionAttachment attachment) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void recalculatePermissions() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean hasGravity() {
    return gravity;
  }

  @Override
  public boolean isOp() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setOp(boolean value) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  @Override
  public PersistentDataContainer getPersistentDataContainer() {
    throw new ClientEntityMethodNotSupportedException();
  }
}
