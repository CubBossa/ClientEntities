package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
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

public class ClientDisplay extends ClientEntity implements Display {

  TrackedField<Integer> interpolationDelay = new TrackedField<>(0);
  TrackedField<Integer> interpolationDuration = new TrackedField<>(0);
  TrackedField<Transformation> transformation = new TrackedField<>(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
  TrackedField<Display.Billboard> billboard = new TrackedField<>(Display.Billboard.FIXED);
  TrackedField<Display.@Nullable Brightness> brightness = new TrackedField<>();
  TrackedField<Float> viewRange = new TrackedField<>(1f);
  TrackedField<Float> shadowRadius = new TrackedField<>(0f);
  TrackedField<Float> shadowStrength = new TrackedField<>(1f);
  TrackedField<Float> displayWidth = new TrackedField<>(0f);
  TrackedField<Float> displayHeight = new TrackedField<>(0f);
  TrackedField<@Nullable Color> glowColorOverride = new TrackedField<>();

  public ClientDisplay(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  @NotNull
  @Override
  public Transformation getTransformation() {
    return transformation.getValue();
  }

  public void setTransformation(@NotNull Transformation transformation) {
    setMeta(this.transformation, transformation);
  }

  public void setTransformationMatrix(@NotNull Matrix4f transformationMatrix) {
    transformationMatrix.getTranslation(this.transformation.getValue().getTranslation());
    transformationMatrix.getScale(this.transformation.getValue().getScale());
    transformationMatrix.getUnnormalizedRotation(this.transformation.getValue().getLeftRotation());
    metaChanged = true;
  }

  @Override
  public int getInterpolationDuration() {
    return interpolationDuration.getValue();
  }

  public void setInterpolationDuration(int duration) {
    setMeta(this.interpolationDuration, duration);
  }

  @Override
  public float getViewRange() {
    return viewRange.getValue();
  }

  public void setViewRange(float range) {
    setMeta(this.viewRange, range);
  }

  @Override
  public float getShadowRadius() {
    return shadowRadius.getValue();
  }

  public void setShadowRadius(float radius) {
    setMeta(this.shadowRadius, radius);
  }

  @Override
  public float getShadowStrength() {
    return shadowStrength.getValue();
  }

  public void setShadowStrength(float strength) {
    setMeta(this.shadowStrength, strength);
  }

  @Override
  public float getDisplayWidth() {
    return displayWidth.getValue();
  }

  public void setDisplayWidth(float width) {
    setMeta(this.displayWidth, width);
  }

  @Override
  public float getDisplayHeight() {
    return displayHeight.getValue();
  }

  public void setDisplayHeight(float height) {
    setMeta(this.displayHeight, height);
  }

  @Override
  public int getInterpolationDelay() {
    return interpolationDelay.getValue();
  }

  public void setInterpolationDelay(int ticks) {
    setMeta(this.interpolationDelay, ticks);
  }

  @NotNull
  @Override
  public Billboard getBillboard() {
    return billboard.getValue();
  }

  public void setBillboard(@NotNull Display.Billboard billboard) {
    setMeta(this.billboard, billboard);
  }

  @Nullable
  @Override
  public Color getGlowColorOverride() {
    return glowColorOverride.getValue();
  }

  public void setGlowColorOverride(@Nullable Color color) {
    setMeta(this.glowColorOverride, color);
  }

  @Nullable
  @Override
  public Brightness getBrightness() {
    return brightness.getValue();
  }

  public void setBrightness(@Nullable Display.Brightness brightness) {
    setMeta(this.brightness, brightness);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (interpolationDelay.hasChanged()) {
      data.add(new EntityData(8, EntityDataTypes.INT, interpolationDelay.getValue()));
    }
    if (interpolationDuration.hasChanged()) {
      data.add(new EntityData(9, EntityDataTypes.INT, interpolationDuration.getValue()));
    }
    if (transformation.hasChanged()) {
      data.add(new EntityData(10, EntityDataTypes.VECTOR3F, convert(transformation.getValue().getTranslation())));
      data.add(new EntityData(11, EntityDataTypes.VECTOR3F, convert(transformation.getValue().getScale())));
      data.add(new EntityData(12, EntityDataTypes.QUATERNION, convert(transformation.getValue().getLeftRotation())));
      data.add(new EntityData(13, EntityDataTypes.QUATERNION, convert(transformation.getValue().getRightRotation())));
    }
    if (billboard.hasChanged()) {
      data.add(new EntityData(14, EntityDataTypes.BYTE, (byte) billboard.getValue().ordinal()));
    }
    if (brightness.hasChanged()) {
      data.add(new EntityData(15, EntityDataTypes.INT, brightness.getValue() == null
          ? -1 : brightness.getValue().getBlockLight() << 4 | brightness.getValue().getSkyLight() << 20));
    }
    if (viewRange.hasChanged()) {
      data.add(new EntityData(16, EntityDataTypes.FLOAT, viewRange.getValue()));
    }
    if (shadowRadius.hasChanged()) {
      data.add(new EntityData(17, EntityDataTypes.FLOAT, shadowRadius.getValue()));
    }
    if (shadowStrength.hasChanged()) {
      data.add(new EntityData(18, EntityDataTypes.FLOAT, shadowStrength.getValue()));
    }
    if (displayWidth.hasChanged()) {
      data.add(new EntityData(19, EntityDataTypes.FLOAT, displayWidth.getValue()));
    }
    if (displayHeight.hasChanged()) {
      data.add(new EntityData(20, EntityDataTypes.FLOAT, displayHeight.getValue()));
    }
    if (glowColorOverride.hasChanged()) {
      data.add(new EntityData(21, EntityDataTypes.INT, glowColorOverride.getValue() == null
          ? -1 : glowColorOverride.getValue().asRGB()));
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
