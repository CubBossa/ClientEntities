package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.UntickedEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@Setter

public class ClientEntity implements UntickedEntity {

  static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

  final int entityId;
  final UUID uniqueId;
  final EntityType type;
  final PlayerSpaceImpl playerSpace;

  boolean alive = true;
  Location previousLocation = null;
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
  boolean elytraFlying = false;
  int airTicks = 300;
  boolean silent = true;
  boolean gravity = true;
  Pose pose = Pose.STANDING;
  boolean frozen = false;

  boolean invulnerable = true;

  boolean customNameVisible = false;
  Component customName;

  // changed properties
  boolean aliveChanged = false;
  boolean locationChanged = false;
  boolean velocityChanged = false;
  boolean metaChanged = false;
  List<Byte> statusEffects = new LinkedList<>();
  List<Consumer<Player>> runnableEffects = new LinkedList<>();

  public ClientEntity(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    this.playerSpace = playerSpace;
    this.entityId = entityId;
    this.uniqueId = createId();
    this.type = entityType;

    // entity not yet spawned
    this.aliveChanged = true;
  }

  public UUID createId() {
    return UUID.randomUUID();
  }

  void spawn(Player player) {
    PacketEvents.getAPI().getPlayerManager().sendPacket(player,
        new WrapperPlayServerSpawnEntity(entityId, Optional.ofNullable(uniqueId), SpigotConversionUtil.fromBukkitEntityType(type),
            new Vector3d(location.getX(), location.getY(), location.getZ()), 0, 0, 0, data(),
            Optional.ofNullable(velocity == null ? null : new Vector3d(velocity.getX(), velocity.getY(), velocity.getZ()))
        )
    );
  }

  @Override
  public void announce(Collection<Player> viewers) {
    for (Player player : viewers) {
      PacketEventsAPI<?> api = PacketEvents.getAPI();

      // Spawn entity
      if (aliveChanged && alive) {
        spawn(player);
        aliveChanged = false;
        locationChanged = false;
      }
      // Play Status Effects on Entity - before delete, for example rocket detonation.
      if (!statusEffects.isEmpty()) {
        statusEffects.forEach(id -> api.getPlayerManager().sendPacket(player, new WrapperPlayServerEntityStatus(entityId, id)));
        statusEffects.clear();
      }
      // Play Sound Effects on Entity before it removal
      if (!runnableEffects.isEmpty()) {
        runnableEffects.forEach(c -> c.accept(player));
        runnableEffects.clear();
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
      if (locationChanged && location != null && !location.equals(previousLocation)) {
        if (location.toVector().equals(previousLocation.toVector())) {
          api.getPlayerManager().sendPacket(player,
              new WrapperPlayServerEntityRotation(entityId, location.getYaw(), location.getPitch(), true)
          );
        } else if (location.distanceSquared(previousLocation) < 64) {
          if (location.getDirection().equals(previousLocation.getDirection())) {
            api.getPlayerManager().sendPacket(player,
                new WrapperPlayServerEntityRelativeMove(entityId,
                    location.getX() - previousLocation.getX(),
                    location.getY() - previousLocation.getY(),
                    location.getZ() - previousLocation.getZ(),
                    true
                )
            );
          } else {
            api.getPlayerManager().sendPacket(player,
                new WrapperPlayServerEntityRelativeMoveAndRotation(entityId,
                    location.getX() - previousLocation.getX(),
                    location.getY() - previousLocation.getY(),
                    location.getZ() - previousLocation.getZ(),
                    location.getYaw(),
                    location.getPitch(),
                    true
                )
            );
          }
        } else {
          api.getPlayerManager().sendPacket(player,
              new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), true)
          );
        }

        previousLocation = location;
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
            | (elytraFlying ? 0x80 : 0)
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
    if (frozen) {
      data.add(new EntityData(7, EntityDataTypes.INT, 1));
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
    this.locationChanged = true;
  }

  public boolean teleport(@NotNull Location location) {
    this.location = location.clone();
    this.locationChanged = true;
    return true;
  }

  public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
    this.location = location.clone();
    this.locationChanged = true;
    return true;
  }

  public boolean teleport(@NotNull Entity destination) {
    this.location = destination.getLocation().clone();
    this.locationChanged = true;
    return true;
  }

  public boolean teleport(@NotNull Entity destination, @NotNull PlayerTeleportEvent.TeleportCause cause) {
    this.location = destination.getLocation().clone();
    this.locationChanged = true;
    return true;
  }

  public void setVelocity(Vector velocity) {
    this.velocity = velocity;
    velocityChanged = true;
  }

  public void playSound(Sound sound, SoundCategory category, float volume, float pitch) {
    runnableEffects.add(player -> {
      try {
        com.github.retrooper.packetevents.protocol.sound.SoundCategory cat = com.github.retrooper.packetevents.protocol.sound.SoundCategory.valueOf(category.toString());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntitySoundEffect(sound.ordinal(), cat, entityId, volume, pitch));
      } catch (Throwable t) {
        t.printStackTrace();
      }
    });
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
    runnableEffects.add(player -> {
      PacketEvents.getAPI().getPlayerManager().sendPacket(player,
          new WrapperPlayServerEntityAnimation(entityId, WrapperPlayServerEntityAnimation.EntityAnimationType.getById(animation.id)));
    });
  }

  public void setVisualFire(boolean fire) {
    this.onFire = setMeta(this.onFire, fire);
  }

  public boolean isVisualFire() {
    return onFire;
  }

  public boolean isVisualFreeze() {
    return frozen;
  }

  public void setVisualFreeze(boolean frozen) {
    this.frozen = setMeta(this.frozen, frozen);
  }

  public void remove() {
    alive = false;
    aliveChanged = true;
  }

  public boolean isDead() {
    return !alive;
  }

  @Nullable
  public Entity getPassenger() {
    throw new ClientEntityMethodNotSupportedException();
  }

  public boolean setPassenger(@NotNull Entity passenger) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  public List<Entity> getPassengers() {
    throw new ClientEntityMethodNotSupportedException();
  }

  public boolean addPassenger(@NotNull Entity passenger) {
    throw new ClientEntityMethodNotSupportedException();
  }

  public boolean removePassenger(@NotNull Entity passenger) {
    throw new ClientEntityMethodNotSupportedException();
  }

  public boolean isEmpty() {
    return true;
  }

  public boolean eject() {
    throw new ClientEntityMethodNotSupportedException();
  }

  public void playEffect(@NotNull EntityEffect type) {
    statusEffects.add(type.getData());
  }

  @NotNull
  public Sound getSwimSound() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  public Sound getSwimSplashSound() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @NotNull
  public Sound getSwimHighSpeedSplashSound() {
    throw new ClientEntityMethodNotSupportedException();
  }

  public boolean isInsideVehicle() {
    return false;
  }

  public boolean leaveVehicle() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  public Entity getVehicle() {
    return null;
  }

  public void setCustomNameVisible(boolean flag) {
    this.setMeta(this.customNameVisible, flag);
  }

  public boolean isCustomNameVisible() {
    return customNameVisible;
  }

  @NotNull
  public Pose getPose() {
    return pose;
  }

  @NotNull
  public SpawnCategory getSpawnCategory() {
    return SpawnCategory.MISC;
  }

  public boolean hasGravity() {
    return gravity;
  }

  void lookAt(Location location) {
    Location temp = location.clone().subtract(this.location);
    setRotation(temp.getYaw(), temp.getPitch());
  }

  void playSound(Sound sound, SoundCategory category) {
    playSound(sound, category, (float) (Math.random() * .2 + 0.9), (float) (Math.random() * 0.3 + 0.85));
  }
}
