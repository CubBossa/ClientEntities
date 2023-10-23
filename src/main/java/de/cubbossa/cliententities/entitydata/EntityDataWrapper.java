package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class EntityDataWrapper {

  protected static final GsonComponentSerializer SERIALIZER = GsonComponentSerializer.builder().build();

  protected EntityDataWrapper() {
  }

  public static class EntityOptions {
    private boolean onFire = false;
    private boolean isCrouching = false;
    private boolean isSprinting = false;
    private boolean isSwimming = false;
    private boolean isInvisible = false;
    private boolean hasGlowEffect = false;
    private boolean isElytraFlying = false;

    public EntityOptions() {
    }

    public EntityOptions(int mask) {
      onFire = (mask & 0x01) == 0x01;
      isCrouching = (mask & 0x02) == 0x02;
      isSprinting = (mask & 0x08) == 0x08;
      isSwimming = (mask & 0x10) == 0x10;
      isInvisible = (mask & 0x20) == 0x20;
      hasGlowEffect = (mask & 0x40) == 0x40;
      isElytraFlying = (mask & 0x80) == 0x80;
    }

    public EntityData build() {
      return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, (byte)
            (onFire ? 0x01 : 0)
          | (isCrouching ? 0x02 : 0)
          | (isSprinting ? 0x08 : 0)
          | (isSwimming ? 0x10 : 0)
          | (isInvisible ? 0x20 : 0)
          | (hasGlowEffect ? 0x40 : 0)
          | (isElytraFlying ? 0x80 : 0)
      ) {
        @Override
        protected int versionedIndex() {
          return 0;
        }
      };
    }

    public void setOnFire(boolean onFire) {
      this.onFire = onFire;
    }

    public void setCrouching(boolean crouching) {
      isCrouching = crouching;
    }

    public void setSprinting(boolean sprinting) {
      isSprinting = sprinting;
    }

    public void setSwimming(boolean swimming) {
      isSwimming = swimming;
    }

    public void setInvisible(boolean invisible) {
      isInvisible = invisible;
    }

    public void setHasGlowEffect(boolean hasGlowEffect) {
      this.hasGlowEffect = hasGlowEffect;
    }

    public void setElytraFlying(boolean elytraFlying) {
      isElytraFlying = elytraFlying;
    }
  }

  public static class RemainingAir extends AbstractEntityDataWrapper {

    public RemainingAir(int ticks) {
      super(EntityDataTypes.INT, ticks);
    }

    @Override
    protected int versionedIndex() {
      return 1;
    }
  }

  public static class CustomName extends AbstractEntityDataWrapper {

    public CustomName(Component component) {
      this(SERIALIZER.serialize(component));
    }

    public CustomName(String component) {
      super(EntityDataTypes.OPTIONAL_COMPONENT, component);
    }

    @Override
    protected int versionedIndex() {
      return 2;
    }
  }

  public static class CustomNameVisible extends AbstractEntityDataWrapper {
    public CustomNameVisible(boolean visible) {
      super(EntityDataTypes.BOOLEAN, visible);
    }

    @Override
    protected int versionedIndex() {
      return 3;
    }
  }

  public static class Silent extends AbstractEntityDataWrapper {
    public Silent(boolean silent) {
      super(EntityDataTypes.BOOLEAN, silent);
    }

    @Override
    protected int versionedIndex() {
      return 4;
    }
  }

  public static class NoGravity extends AbstractEntityDataWrapper {
    public NoGravity(boolean noGravity) {
      super(EntityDataTypes.BOOLEAN, noGravity);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
        return 5;
      }
      return -1;
    }
  }

  public static class Pose extends AbstractEntityDataWrapper {
    public Pose(EntityPose pose) {
      super(EntityDataTypes.ENTITY_POSE, pose);
    }

    @Override
    protected int versionedIndex() {
      return 6;
    }
  }

  public static class FrozenTicks extends AbstractEntityDataWrapper {
    public FrozenTicks(EntityDataType<?> type, Object value) {
      super(type, value);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isOlderThan(ServerVersion.V_1_17)) {
        return -1;
      }
      return 7;
    }
  }
}
