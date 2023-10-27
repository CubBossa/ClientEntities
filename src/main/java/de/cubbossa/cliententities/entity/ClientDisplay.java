package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
import de.cubbossa.cliententities.entitydata.DisplayDataWrapper;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.List;

public class ClientDisplay extends ClientEntity implements Display {

  TrackedField<Integer> interpolationDelay = new TrackedField<>(0);
  TrackedField<Integer> interpolationDuration = new TrackedField<>(0);
  TrackedField<Quaternionf> leftRotation = new TrackedField<>(new Quaternionf(new AxisAngle4f(0, 1, 0, 0)));
  TrackedField<Vector3f> translation = new TrackedField<>(new Vector3f(0, 0, 0));
  TrackedField<Vector3f> scale = new TrackedField<>(new Vector3f(1, 1, 1));
  TrackedField<Quaternionf> rightRotation = new TrackedField<>(new Quaternionf(new AxisAngle4f(0, 1, 0, 0)));
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
    return new Transformation(new Vector3f(translation.getValue()), new Quaternionf(leftRotation.getValue()), new Vector3f(scale.getValue()), new Quaternionf(rightRotation.getValue()));
  }

  @Override
  public void setTransformation(@NotNull Transformation transformation) {
    setMeta(translation, transformation.getTranslation());
    setMeta(leftRotation, transformation.getLeftRotation());
    setMeta(scale, transformation.getScale());
    setMeta(rightRotation, transformation.getRightRotation());
  }

  @Override
  public void setTransformationMatrix(@NotNull Matrix4f transformationMatrix) {
    Vector3f _translation = new Vector3f();
    transformationMatrix.getTranslation(_translation);
    setMeta(translation, _translation);
    AxisAngle4f _leftRotation = new AxisAngle4f();
    transformationMatrix.getRotation(_leftRotation);
    setMeta(leftRotation, new Quaternionf(_leftRotation));
    Vector3f _scale = new Vector3f();
    transformationMatrix.getScale(_scale);
    setMeta(scale, _scale);
  }

  @Override
  public int getInterpolationDuration() {
    return interpolationDuration.getValue();
  }

  @Override
  public void setInterpolationDuration(int duration) {
    setMeta(this.interpolationDuration, duration);
  }

  @Override
  public float getViewRange() {
    return viewRange.getValue();
  }

  @Override
  public void setViewRange(float range) {
    setMeta(this.viewRange, range);
  }

  @Override
  public float getShadowRadius() {
    return shadowRadius.getValue();
  }

  @Override
  public void setShadowRadius(float radius) {
    setMeta(this.shadowRadius, radius);
  }

  @Override
  public float getShadowStrength() {
    return shadowStrength.getValue();
  }

  @Override
  public void setShadowStrength(float strength) {
    setMeta(this.shadowStrength, strength);
  }

  @Override
  public float getDisplayWidth() {
    return displayWidth.getValue();
  }

  @Override
  public void setDisplayWidth(float width) {
    setMeta(this.displayWidth, width);
  }

  @Override
  public float getDisplayHeight() {
    return displayHeight.getValue();
  }

  @Override
  public void setDisplayHeight(float height) {
    setMeta(this.displayHeight, height);
  }

  @Override
  public int getInterpolationDelay() {
    return interpolationDelay.getValue();
  }

  @Override
  public void setInterpolationDelay(int ticks) {
    setMeta(this.interpolationDelay, ticks);
  }

  @NotNull
  @Override
  public Billboard getBillboard() {
    return billboard.getValue();
  }

  @Override
  public void setBillboard(@NotNull Display.Billboard billboard) {
    setMeta(this.billboard, billboard);
  }

  @Nullable
  @Override
  public Color getGlowColorOverride() {
    return glowColorOverride.getValue();
  }

  @Override
  public void setGlowColorOverride(@Nullable Color color) {
    setMeta(this.glowColorOverride, color);
  }

  @Nullable
  @Override
  public Brightness getBrightness() {
    return brightness.getValue();
  }

  @Override
  public void setBrightness(@Nullable Display.Brightness brightness) {
    setMeta(this.brightness, brightness);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (interpolationDelay.hasChanged()) {
      data.add(new DisplayDataWrapper.InterpolationDelay(interpolationDelay.getValue()));
      interpolationDelay.flushChanged();
    }
    if (interpolationDuration.hasChanged()) {
      data.add(new DisplayDataWrapper.InterpolationDuration(interpolationDuration.getValue()));
      interpolationDuration.flushChanged();
    }
    if(translation.hasChanged()) {
      data.add(new DisplayDataWrapper.Translation(convert(translation.getValue())));
      translation.flushChanged();
    }
    if(leftRotation.hasChanged()) {
      data.add(new DisplayDataWrapper.LeftRotation(convert(leftRotation.getValue())));
      leftRotation.flushChanged();
    }
    if(scale.hasChanged()) {
      data.add(new DisplayDataWrapper.Scale(convert(scale.getValue())));
      scale.flushChanged();
    }
    if(rightRotation.hasChanged()) {
      data.add(new DisplayDataWrapper.RightRotation(convert(rightRotation.getValue())));
      rightRotation.flushChanged();
    }
    if (billboard.hasChanged()) {
      data.add(new EntityData(14, EntityDataTypes.BYTE, (byte) billboard.getValue().ordinal()));
      billboard.flushChanged();
    }
    if (brightness.hasChanged()) {
      if (brightness.getValue() == null) {
        data.add(new DisplayDataWrapper.BrightnessOverride());
      } else {
        data.add(new DisplayDataWrapper.BrightnessOverride(brightness.getValue().getBlockLight(), brightness.getValue().getSkyLight()));
      }
      brightness.flushChanged();
    }
    if (viewRange.hasChanged()) {
      data.add(new DisplayDataWrapper.ViewRange(viewRange.getValue()));
      viewRange.flushChanged();
    }
    if (shadowRadius.hasChanged()) {
      data.add(new DisplayDataWrapper.ShadowRadius(shadowRadius.getValue()));
      shadowRadius.flushChanged();
    }
    if (shadowStrength.hasChanged()) {
      data.add(new DisplayDataWrapper.ShadowStrength(shadowStrength.getValue()));
      shadowRadius.flushChanged();
    }
    if (displayWidth.hasChanged()) {
      data.add(new DisplayDataWrapper.Width(displayWidth.getValue()));
      displayWidth.flushChanged();
    }
    if (displayHeight.hasChanged()) {
      data.add(new DisplayDataWrapper.Height(displayHeight.getValue()));
      displayHeight.flushChanged();
    }
    if (glowColorOverride.hasChanged()) {
      if (glowColorOverride.getValue() == null) {
        data.add(new DisplayDataWrapper.GlowColorOverride());
      } else {
        data.add(new DisplayDataWrapper.GlowColorOverride(glowColorOverride.getValue()));
      }
      glowColorOverride.flushChanged();
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
