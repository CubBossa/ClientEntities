package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import de.cubbossa.cliententities.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pose;
import org.bukkit.entity.SpawnCategory;
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

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@NotThreadSafe
public class ClientEntity implements ClientViewElement, Entity {

  public static class DeletePacketInfo implements CombineInfo {

    protected HashSet<Integer> ids = new HashSet<>();

    @Override
    public CombineInfo merge(CombineInfo other) {
      if (!(other instanceof DeletePacketInfo)) {
        throw new IllegalArgumentException("both combine infos must be of the same type");
      }
      DeletePacketInfo i = new DeletePacketInfo();
      i.ids.addAll(this.ids);
      return i;
    }

    @Override
    public PacketWrapper<?> wrapper() {
      return new WrapperPlayServerDestroyEntities(ids.stream().mapToInt(Integer::intValue).toArray());
    }
  }

  static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

  final int entityId;
  final UUID uniqueId;
  final EntityType type;
  final PlayerSpaceImpl playerSpace;

  TrackedBoolField alive = new TrackedBoolField(false);
  Location previousLocation = null;
  Location location = new Location(null, 0, 0, 0);
  TrackedField<Vector> velocity = new TrackedField<>(new Vector(0, 0, 0));
  TrackedField<Double> height = new TrackedField<>(0d);
  TrackedField<Double> width = new TrackedField<>(0d);
  TrackedBoolField onFire = new TrackedBoolField();
  TrackedBoolField crouching = new TrackedBoolField();
  TrackedBoolField sprinting = new TrackedBoolField();
  TrackedBoolField swimming = new TrackedBoolField();
  TrackedBoolField invisible = new TrackedBoolField();
  TrackedBoolField glowing = new TrackedBoolField();
  TrackedBoolField elytraFlying = new TrackedBoolField();
  TrackedField<Integer> airTicks = new TrackedField<>(300);
  TrackedBoolField silent = new TrackedBoolField(true);
  TrackedBoolField gravity = new TrackedBoolField(true);
  TrackedField<Pose> pose = new TrackedField<>(Pose.STANDING);
  TrackedBoolField frozen = new TrackedBoolField();

  TrackedBoolField customNameVisible = new TrackedBoolField();
  TrackedField<Component> customName = new TrackedField<>();

  TrackedField<@Nullable Entity> vehicle = new TrackedField<>();
  List<Integer> passengers = new ArrayList<>();
  boolean passengersChanged = false;

  List<Supplier<PacketInfo>> statelessEffect = new LinkedList<>();

  boolean metaChanged = false;

  public ClientEntity(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    this.playerSpace = playerSpace;
    this.entityId = entityId;
    this.uniqueId = createId();
    this.type = entityType;

    // entity not yet spawned
    alive.setValue(true);
  }

  public UUID createId() {
    return UUID.randomUUID();
  }

  @Override
  public List<UpdateInfo> spawn(boolean onlyIfChanged) {
    if (!onlyIfChanged || alive.hasChanged() && alive.getBooleanValue()) {
      alive.flushChanged();
      return spawnPacket().stream().map(PacketInfo::packet).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  List<PacketWrapper<?>> spawnPacket() {
    return Collections.singletonList(new WrapperPlayServerSpawnEntity(entityId, Optional.ofNullable(uniqueId), SpigotConversionUtil.fromBukkitEntityType(type),
        new Vector3d(location.getX(), location.getY(), location.getZ()), 0, 0, 0, data(),
        Optional.ofNullable(velocity == null ? null : new Vector3d(velocity.getValue().getX(), velocity.getValue().getY(), velocity.getValue().getZ()))
    ));
  }

  @Override
  public List<UpdateInfo> delete(boolean onlyIfChanged) {
    if (!onlyIfChanged || alive.hasChanged() && !alive.getBooleanValue()) {
      DeletePacketInfo info = new DeletePacketInfo();
      info.ids.add(this.entityId);
      alive.flushChanged();
      playerSpace.releaseEntity(this);
      return Collections.singletonList(info);
    }
    return Collections.emptyList();
  }

  @Override
  public List<UpdateInfo> state(boolean onlyIfChanged) {
    List<UpdateInfo> result = new ArrayList<>();

    if (!statelessEffect.isEmpty()) {
      statelessEffect.forEach(c -> result.add(c.get()));
      statelessEffect.clear();
    }

    // Change location
    if (location != null && !location.equals(previousLocation)) {
      if (previousLocation != null && location.toVector().equals(previousLocation.toVector())) {
        result.add(PacketInfo.packet(new WrapperPlayServerEntityRotation(entityId, location.getYaw(), location.getPitch(), true)));
      } else if (previousLocation != null && location.distanceSquared(previousLocation) < 64) {
        if (location.getDirection().equals(previousLocation.getDirection())) {
          result.add(PacketInfo.packet(new WrapperPlayServerEntityRelativeMove(entityId,
              location.getX() - previousLocation.getX(),
              location.getY() - previousLocation.getY(),
              location.getZ() - previousLocation.getZ(),
              true
          )));
        } else {
          result.add(PacketInfo.packet(new WrapperPlayServerEntityRelativeMoveAndRotation(entityId,
              location.getX() - previousLocation.getX(),
              location.getY() - previousLocation.getY(),
              location.getZ() - previousLocation.getZ(),
              location.getYaw(),
              location.getPitch(),
              true
          )));
        }
      } else {
        result.add(PacketInfo.packet(
            new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), true)
        ));
      }

      previousLocation = location.clone();
    }

    // change velocity
    if (velocity.hasChanged() && velocity.getValue() != null) {
      result.add(PacketInfo.packet(
          new WrapperPlayServerEntityVelocity(entityId, new Vector3d(velocity.getValue().getX(), velocity.getValue().getY(), velocity.getValue().getZ()))
      ));
      velocity.flushChanged();
    }

    // Change Meta Data
    if (metaChanged) {
      try {
        List<EntityData> data = metaData();
        result.add(PacketInfo.packet(new WrapperPlayServerEntityMetadata(entityId, data)));
      } catch (Throwable t) {
        t.printStackTrace();
      }
      metaChanged = false;
    }

    // Update Passengers
    if (passengersChanged) {
      result.add(PacketInfo.packet(
          new WrapperPlayServerSetPassengers(entityId, passengers.stream().mapToInt(Integer::intValue).toArray())
      ));
      passengersChanged = false;
    }
    return result;
  }

  int data() {
    return 0;
  }

  TrackedMask metaMask = new TrackedMask(
      onFire, crouching, null, sprinting, swimming, invisible, glowing, elytraFlying
  );

  List<EntityData> metaData() {
    List<EntityData> data = new ArrayList<>();

    if (metaMask.hasChanged()) {
      data.add(new EntityData(0, EntityDataTypes.BYTE, metaMask.byteVal()));
    }
    if (airTicks.hasChanged()) {
      data.add(new EntityData(1, EntityDataTypes.INT, airTicks));
    }
    // data.add(new EntityData(2, EntityDataTypes.OPTIONAL_COMPONENT, Optional.ofNullable(getCustomName())));
    if (customNameVisible.hasChanged()) {
      data.add(new EntityData(3, EntityDataTypes.BOOLEAN, true));
    }
    if (silent.hasChanged()) {
      data.add(new EntityData(4, EntityDataTypes.BOOLEAN, silent.getBooleanValue()));
    }
    if (gravity.hasChanged()) {
      data.add(new EntityData(5, EntityDataTypes.BOOLEAN, !gravity.getBooleanValue()));
    }
    if (pose.hasChanged()) {
      data.add(new EntityData(6, EntityDataTypes.ENTITY_POSE, pose.getValue()));
    }
    if (frozen.hasChanged()) {
      data.add(new EntityData(7, EntityDataTypes.INT, frozen.getBooleanValue() ? 1 : 0));
    }
    return data;
  }

  protected <T> void setMeta(TrackedField<T> field, T val) {
    field.setValue(val);
    if (field.hasChanged()) {
      metaChanged = true;
    }
  }

  public void setHeight(double height) {
    this.height.setValue(height);
  }

  public void setWidth(double width) {
    this.width.setValue(width);
  }

  public void setCustomName(Component customName) {
    setMeta(this.customName, customName);
  }

  public void setCustomName(String customName) {
    setCustomName(Component.text(customName));
  }

  public String getCustomName() {
    return GSON.serialize(customName.getValue());
  }

  @NotNull
  public Location getLocation() {
    return location;
  }

  @Nullable
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
  public World getWorld() {
    return getLocation().getWorld();
  }

  public void setRotation(float yaw, float pitch) {
    this.location.setYaw(yaw);
    this.location.setPitch(pitch);
  }

  public boolean teleport(@NotNull Location location) {
    this.location = location.clone();
    return true;
  }

  public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
    this.location = location.clone();
    return true;
  }

  public boolean teleport(@NotNull Entity destination) {
    this.location = destination.getLocation().clone();
    return true;
  }

  public boolean teleport(@NotNull Entity destination, @NotNull PlayerTeleportEvent.TeleportCause cause) {
    this.location = destination.getLocation().clone();
    return true;
  }

  @NotNull
  @Override
  @Deprecated
  public List<Entity> getNearbyEntities(double v, double v1, double v2) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public int getEntityId() {
    return entityId;
  }

  @Override
  @Deprecated
  public int getFireTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getMaxFireTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setFireTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  public void setVelocity(@NotNull Vector velocity) {
    this.velocity.setValue(velocity);
  }

  @NotNull
  @Override
  public Vector getVelocity() {
    return velocity.getValue();
  }

  @Override
  public double getHeight() {
    return height.getValue();
  }

  @Override
  public double getWidth() {
    return width.getValue();
  }

  @NotNull
  @Override
  @Deprecated
  public BoundingBox getBoundingBox() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isOnGround() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isInWater() {
    throw new ServerSideMethodNotSupported();
  }

  public void playSound(Sound sound, SoundCategory category, float volume, float pitch) {
    statelessEffect.add(() -> {
      com.github.retrooper.packetevents.protocol.sound.SoundCategory cat = com.github.retrooper.packetevents.protocol.sound.SoundCategory.valueOf(category.toString());
      return PacketInfo.packet(new WrapperPlayServerEntitySoundEffect(sound.ordinal(), cat, entityId, volume, pitch));
    });
  }

  @Override
  @Deprecated
  public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public List<MetadataValue> getMetadata(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean hasMetadata(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isPermissionSet(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isPermissionSet(@NotNull Permission permission) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean hasPermission(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean hasPermission(@NotNull Permission permission) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void recalculatePermissions() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isOp() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setOp(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public PersistentDataContainer getPersistentDataContainer() {
    throw new ServerSideMethodNotSupported();
  }

  public boolean isAliveChanged() {
    return this.alive.getValue();
  }

  public void setAliveChanged(boolean changed) {
    this.alive.overrideChanged(changed);
  }

  enum Animation {
    SWING_MAIN_ARM(0),
    LEAVE_BED(2),
    SWING_OFFHAND(3),
    CRITICAL_EFFECT(4),
    MAGIC_CRITICAL_EFFECT(5);

    int id;

    Animation(int id) {
      this.id = id;
    }

    public int id() {
      return id;
    }
  }

  void playAnimation(Animation animation) {
    statelessEffect.add(() -> PacketInfo.packet(
        new WrapperPlayServerEntityAnimation(entityId, WrapperPlayServerEntityAnimation.EntityAnimationType.getById(animation.id))
    ));
  }

  public void setVisualFire(boolean fire) {
    setMeta(this.onFire, fire);
  }

  public boolean isVisualFire() {
    return onFire.getBooleanValue();
  }

  @Override
  @Deprecated
  public int getFreezeTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getMaxFreezeTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setFreezeTicks(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean isFrozen() {
    return false;
  }

  public boolean isVisualFreeze() {
    return frozen.getBooleanValue();
  }

  public void setVisualFreeze(boolean frozen) {
    setMeta(this.frozen, frozen);
  }

  public void remove() {
    alive.setValue(false);
  }

  public boolean isDead() {
    return !alive.getBooleanValue();
  }

  @Override
  @Deprecated
  public boolean isValid() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void sendMessage(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void sendMessage(@NotNull String... strings) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public Server getServer() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  public String getName() {
    return null;
  }

  @Override
  public boolean isPersistent() {
    return false;
  }

  @Override
  @Deprecated
  public void setPersistent(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  public Entity getPassenger() {
    if (passengers.isEmpty()) {
      return null;
    }
    return playerSpace.getEntity(passengers.get(0));
  }

  public boolean setPassenger(@NotNull Entity passenger) {
    passengers.clear();
    passengers.add(passenger.getEntityId());
    passengersChanged = true;
    return true;
  }

  @NotNull
  public List<Entity> getPassengers() {
    return passengers.stream()
        .map(playerSpace::getEntity)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public boolean addPassenger(@NotNull Entity passenger) {
    passengersChanged = true;
    return passengers.add(passenger.getEntityId());
  }

  @Deprecated
  public boolean removePassenger(@NotNull Entity passenger) {
    passengersChanged = true;
    return passengers.remove(passenger.getEntityId()) != null;
  }

  public boolean isEmpty() {
    return passengers.isEmpty();
  }

  @Deprecated
  public boolean eject() {
    passengers.clear();
    passengersChanged = true;
    return true;
  }

  @Override
  @Deprecated
  public float getFallDistance() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setFallDistance(float v) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setLastDamageCause(@Nullable EntityDamageEvent entityDamageEvent) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public EntityDamageEvent getLastDamageCause() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  public UUID getUniqueId() {
    return uniqueId;
  }

  @Override
  @Deprecated
  public int getTicksLived() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setTicksLived(int i) {
    throw new ServerSideMethodNotSupported();
  }

  public void playEffect(@NotNull EntityEffect type) {
    statelessEffect.add(() -> PacketInfo.packet(
        new WrapperPlayServerEntityStatus(entityId, type.getData()))
    );
  }

  @NotNull
  @Override
  public EntityType getType() {
    return type;
  }

  @NotNull
  @Deprecated
  public Sound getSwimSound() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Deprecated
  public Sound getSwimSplashSound() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Deprecated
  public Sound getSwimHighSpeedSplashSound() {
    throw new ServerSideMethodNotSupported();
  }

  public boolean isInsideVehicle() {
    return vehicle.getValue() != null;
  }

  @Deprecated
  public boolean leaveVehicle() {
    boolean inside = isInsideVehicle();
    if (inside) {
      vehicle.getValue().removePassenger(this);
    }
    return inside;
  }

  @Nullable
  public Entity getVehicle() {
    return vehicle.getValue();
  }

  public void setCustomNameVisible(boolean flag) {
    this.setMeta(this.customNameVisible, flag);
  }

  public boolean isCustomNameVisible() {
    return customNameVisible.getBooleanValue();
  }

  @Override
  @Deprecated
  public void setVisibleByDefault(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isVisibleByDefault() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setGlowing(boolean b) {
    setMeta(glowing, b);
  }

  @Override
  public boolean isGlowing() {
    return glowing.getBooleanValue();
  }

  @Override
  public void setInvulnerable(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean isInvulnerable() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean isSilent() {
    return silent.getBooleanValue();
  }

  @Override
  public void setSilent(boolean b) {
    setMeta(silent, b);
  }

  @NotNull
  public Pose getPose() {
    return pose.getValue();
  }

  @NotNull
  public SpawnCategory getSpawnCategory() {
    return SpawnCategory.MISC;
  }

  @NotNull
  @Override
  @Deprecated
  public Spigot spigot() {
    throw new ServerSideMethodNotSupported();
  }

  public boolean hasGravity() {
    return gravity.getBooleanValue();
  }

  @Override
  public void setGravity(boolean b) {
    setMeta(gravity, b);
  }

  @Override
  @Deprecated
  public int getPortalCooldown() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setPortalCooldown(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public Set<String> getScoreboardTags() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean addScoreboardTag(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean removeScoreboardTag(@NotNull String s) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public PistonMoveReaction getPistonMoveReaction() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public BlockFace getFacing() {
    throw new ServerSideMethodNotSupported();
  }

  void lookAt(Location location) {
    Location temp = location.clone().subtract(this.location);
    setRotation(temp.getYaw(), temp.getPitch());
  }

  void playSound(Sound sound, SoundCategory category) {
    playSound(sound, category, (float) (Math.random() * .2 + 0.9), (float) (Math.random() * 0.3 + 0.85));
  }
}
