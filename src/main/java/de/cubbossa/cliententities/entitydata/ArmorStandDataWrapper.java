package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;

public class ArmorStandDataWrapper extends LivingEntityDataWrapper {

  private static int poseIndex(int offset) {
    ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
    if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
      return 16 + offset;
    }
    if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
      return 15 + offset;
    }
    if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
      return 14 + offset;
    }
    if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
      return 12 + offset;
    }
    return 11 + offset;
  }

  public static Options options() {
    return new Options(0);
  }

  public static Options options(int mask) {
    return new Options(mask);
  }

  public static class Options {
    private boolean isSmall;
    private boolean hasGravity;
    private boolean hasArms;
    private boolean noBasePlate;
    private boolean marker;

    private Options(int mask) {
      this.isSmall = (mask & 0x01) == 0x01;
      this.hasGravity = (mask & 0x02) == 0x02;
      this.hasArms = (mask & 0x04) == 0x04;
      this.noBasePlate = (mask & 0x08) == 0x08;
      this.marker = (mask & 0x10) == 0x10;
    }

    public EntityData build() {
      ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
      return new EntityData(poseIndex(-1), EntityDataTypes.BYTE, (byte) (
          (this.isSmall ? 0x01 : 0)
              | (this.hasGravity && serverVersion.isOlderThanOrEquals(ServerVersion.V_1_9) ? 0x02 : 0)
              | (this.hasArms ? 0x04 : 0)
              | (this.noBasePlate ? 0x08 : 0)
              | (this.marker ? 0x10 : 0)
      ));
    }

    public boolean isSmall() {
      return isSmall;
    }

    public Options setSmall(boolean small) {
      isSmall = small;
      return this;
    }

    public boolean hasGravity() {
      return hasGravity;
    }

    public Options setHasGravity(boolean hasGravity) {
      this.hasGravity = hasGravity;
      return this;
    }

    public boolean hasArms() {
      return hasArms;
    }

    public Options setHasArms(boolean hasArms) {
      this.hasArms = hasArms;
      return this;
    }

    public boolean isNoBasePlate() {
      return noBasePlate;
    }

    public Options setNoBasePlate(boolean noBasePlate) {
      this.noBasePlate = noBasePlate;
      return this;
    }

    public boolean isMarker() {
      return marker;
    }

    public Options setMarker(boolean marker) {
      this.marker = marker;
      return this;
    }
  }

  public static EntityData headPose(Vector3f rotation) {
    return new EntityData(poseIndex(0), EntityDataTypes.ROTATION, rotation);
  }

  public static EntityData bodyPose(Vector3f rotation) {
    return new EntityData(poseIndex(1), EntityDataTypes.ROTATION, rotation);
  }

  public static EntityData leftArmPose(Vector3f rotation) {
    return new EntityData(poseIndex(2), EntityDataTypes.ROTATION, rotation);
  }

  public static EntityData rightArmPose(Vector3f rotation) {
    return new EntityData(poseIndex(3), EntityDataTypes.ROTATION, rotation);
  }

  public static EntityData leftLegPose(Vector3f rotation) {
    return new EntityData(poseIndex(4), EntityDataTypes.ROTATION, rotation);
  }

  public static EntityData rightLegPose(Vector3f rotation) {
    return new EntityData(poseIndex(5), EntityDataTypes.ROTATION, rotation);
  }
}
