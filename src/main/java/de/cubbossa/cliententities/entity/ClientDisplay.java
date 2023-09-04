package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import de.cubbossa.cliententities.PlayerSpace;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

@Getter
public class ClientDisplay extends ClientEntity {

  int interpolationDelay = 0;
  int interpolationDuration = 0;
  Transformation transformation = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f());
  Display.Billboard billboard = Display.Billboard.FIXED;
  @Nullable Display.Brightness brightness = null;
  float viewRange = 1;
  float shadowRadius = 0;
  float shadowStrength = 1;
  float displayWidth = 0;
  float displayHeight = 0;
  @Nullable Color glowColorOverride = null;

  public ClientDisplay(PlayerSpace playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  public void setTransformation(@NotNull Transformation transformation) {
    if (this.transformation.equals(transformation)) {
      return;
    }
    this.transformation = transformation;
    metaChanged = true;
  }

  public void setTransformationMatrix(@NotNull Matrix4f transformationMatrix) {
    transformationMatrix.getTranslation(this.transformation.getTranslation());
    transformationMatrix.getScale(this.transformation.getScale());
    transformationMatrix.getUnnormalizedRotation(this.transformation.getLeftRotation());
    metaChanged = true;
  }

  public void setInterpolationDuration(int duration) {
    this.interpolationDuration = setMeta(this.interpolationDuration, duration);
  }

  public void setViewRange(float range) {
    this.viewRange = setMeta(this.viewRange, range);
  }

  public void setShadowRadius(float radius) {
    this.shadowRadius = setMeta(this.shadowRadius, radius);
  }

  public void setShadowStrength(float strength) {
    this.shadowStrength = setMeta(this.shadowStrength, strength);
  }

  public void setDisplayWidth(float width) {
    this.displayWidth = setMeta(this.displayWidth, width);
  }

  public void setDisplayHeight(float height) {
    this.displayHeight = setMeta(this.displayHeight, height);
  }

  public void setInterpolationDelay(int ticks) {
    this.interpolationDelay = setMeta(this.interpolationDelay, ticks);
  }

  public void setBillboard(@NotNull Display.Billboard billboard) {
    this.billboard = setMeta(this.billboard, billboard);
  }

  public void setGlowColorOverride(@Nullable Color color) {
    this.glowColorOverride = setMeta(this.glowColorOverride, color);
  }

  public void setBrightness(@Nullable Display.Brightness brightness) {
    this.brightness = setMeta(this.brightness, brightness);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (interpolationDelay != 0) {
      data.add(new EntityData(8, EntityDataTypes.INT, interpolationDelay));
    }
    if (interpolationDuration != 0) {
      data.add(new EntityData(9, EntityDataTypes.INT, interpolationDuration));
    }
    if (!transformation.getTranslation().equals(new Vector3f(0, 0, 0))) {
      data.add(new EntityData(10, EntityDataTypes.VECTOR3F, convert(transformation.getTranslation())));
    }
    if (!transformation.getScale().equals(new Vector3f(1, 1, 1))) {
      data.add(new EntityData(11, EntityDataTypes.VECTOR3F, convert(transformation.getScale())));
    }
    data.add(new EntityData(12, EntityDataTypes.QUATERNION, convert(transformation.getLeftRotation())));
    data.add(new EntityData(13, EntityDataTypes.QUATERNION, convert(transformation.getRightRotation())));
    if (billboard != Display.Billboard.FIXED) {
      data.add(new EntityData(14, EntityDataTypes.BYTE, (byte) billboard.ordinal()));
    }
    if (brightness != null) {
      data.add(new EntityData(15, EntityDataTypes.INT, brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20));
    }
    if (viewRange != 1) {
      data.add(new EntityData(16, EntityDataTypes.FLOAT, viewRange));
    }
    if (shadowRadius != 0) {
      data.add(new EntityData(17, EntityDataTypes.FLOAT, shadowRadius));
    }
    if (shadowStrength != 1) {
      data.add(new EntityData(18, EntityDataTypes.FLOAT, shadowStrength));
    }
    if (displayWidth != 0) {
      data.add(new EntityData(19, EntityDataTypes.FLOAT, displayWidth));
    }
    if (displayHeight != 0) {
      data.add(new EntityData(20, EntityDataTypes.FLOAT, displayHeight));
    }
    if (glowColorOverride != null) {
      data.add(new EntityData(21, EntityDataTypes.INT, glowColorOverride.asRGB()));
    }
    return data;
  }

  Quaternion4f convert(Quaternionf quaternionf) {
    return new Quaternion4f(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w);
  }

  com.github.retrooper.packetevents.util.Vector3f convert(Vector3f vector3f) {
    return new com.github.retrooper.packetevents.util.Vector3f(vector3f.x, vector3f.y, vector3f.z);
  }

}
