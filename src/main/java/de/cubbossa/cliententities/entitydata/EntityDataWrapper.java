package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class EntityDataWrapper {

  protected static final GsonComponentSerializer SERIALIZER = GsonComponentSerializer.builder().build();

  protected EntityDataWrapper() {
  }

  public static EntityOptions optionsBuilder() {
    return new EntityOptions(0);
  }

  public static EntityData options(int mask) {
    return new EntityOptions(mask).build();
  }

  public static class EntityOptions {
    private boolean onFire = false;
    private boolean isCrouching = false;
    private boolean isSprinting = false;
    private boolean isEatingDrinkingBlocking = false;
    private boolean isSwimming = false;
    private boolean isInvisible = false;
    private boolean hasGlowEffect = false;
    private boolean isElytraFlying = false;

    private EntityOptions() {
    }

    private EntityOptions(int mask) {
      onFire = (mask & 0x01) == 0x01;
      isCrouching = (mask & 0x02) == 0x02;
      isSprinting = (mask & 0x08) == 0x08;
      isSwimming = (mask & 0x10) == 0x10;
      isEatingDrinkingBlocking = isSwimming;
      isInvisible = (mask & 0x20) == 0x20;
      hasGlowEffect = (mask & 0x40) == 0x40;
      isElytraFlying = (mask & 0x80) == 0x80;
    }

    public EntityData build() {
      ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();

      return new AbstractEntityDataWrapper(EntityDataTypes.BYTE, (byte)
          (onFire ? 0x01 : 0)
          | (isCrouching ? 0x02 : 0)
          | (isSprinting ? 0x08 : 0)
          | (isSwimming && serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13)
          | isEatingDrinkingBlocking && serverVersion.isOlderThan(ServerVersion.V_1_11_2) ? 0x10 : 0)
          | (isInvisible && serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8) ? 0x20 : 0)
          | (hasGlowEffect && serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8) ? 0x40 : 0)
          | (isElytraFlying && serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8) ? 0x80 : 0)
      ) {
        @Override
        protected int versionedIndex() {
          return 0;
        }
      };
    }

    public EntityOptions setOnFire(boolean onFire) {
      this.onFire = onFire;
      return this;
    }

    public EntityOptions setCrouching(boolean crouching) {
      isCrouching = crouching;
      return this;
    }

    public EntityOptions setSprinting(boolean sprinting) {
      isSprinting = sprinting;
      return this;
    }

    /**
     * Introduced with 1.13
     */
    public EntityOptions setSwimming(boolean swimming) {
      isSwimming = swimming;
      return this;
    }

    /**
     * Only up to 1.11.2
     */
    public EntityOptions setEatingDrinkingBlocking(boolean eatingDrinkingBlocking) {
      isEatingDrinkingBlocking = eatingDrinkingBlocking;
      return this;
    }

    public EntityOptions setInvisible(boolean invisible) {
      isInvisible = invisible;
      return this;
    }

    public EntityOptions setHasGlowEffect(boolean hasGlowEffect) {
      this.hasGlowEffect = hasGlowEffect;
      return this;
    }

    public EntityOptions setElytraFlying(boolean elytraFlying) {
      isElytraFlying = elytraFlying;
      return this;
    }
  }

  public static EntityData remainingAir(int ticks) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, ticks) {
      @Override
      protected int versionedIndex() {
        return 1;
      }
    };
  }

  public static EntityData customName(Component component) {
    return customName(SERIALIZER.serialize(component));
  }

  public static EntityData customName(String componenGson) {
    return new AbstractEntityDataWrapper(EntityDataTypes.OPTIONAL_COMPONENT, componenGson) {
      @Override
      protected int versionedIndex() {
        return 2;
      }
    };
  }

  public static EntityData customNameVisible(boolean visible) {
    return new EntityData(3, EntityDataTypes.BOOLEAN, visible);
  }

  public static EntityData silent(boolean silent) {
    return new EntityData(4, EntityDataTypes.BOOLEAN, silent);
  }

  public static EntityData noGravity(boolean noGravity) {
    return new AbstractEntityDataWrapper(EntityDataTypes.BOOLEAN, noGravity) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
          return 5;
        }
        return -1;
      }
    };
  }

  public static EntityData pose(EntityPose pose) {
    return new AbstractEntityDataWrapper(EntityDataTypes.ENTITY_POSE, pose) {
      @Override
      protected int versionedIndex() {
        return 6; //TODO
      }
    };
  }

  public static EntityData frozenTicks(int ticks) {
    return new AbstractEntityDataWrapper(EntityDataTypes.INT, ticks) {
      @Override
      protected int versionedIndex() {
        if (serverVersion.isOlderThan(ServerVersion.V_1_17)) {
          return -1;
        }
        return 7;
      }
    };
  }
}
