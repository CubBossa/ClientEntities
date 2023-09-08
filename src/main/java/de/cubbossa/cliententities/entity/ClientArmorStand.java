package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import de.cubbossa.cliententities.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientArmorStand extends ClientLivingEntity implements ArmorStand {

  TrackedBoolField small = new TrackedBoolField();
  TrackedBoolField basePlate = new TrackedBoolField(true);
  TrackedBoolField marker = new TrackedBoolField();
  TrackedBoolField arms = new TrackedBoolField();
  TrackedMask armorStandMask = new TrackedMask(small, arms, basePlate, marker);

  TrackedField<EulerAngle> headPose = new TrackedField<>(new EulerAngle(0, 0, 0));
  TrackedField<EulerAngle> bodyPose = new TrackedField<>(new EulerAngle(0, 0, 0));
  TrackedField<EulerAngle> rightArmPose = new TrackedField<>(new EulerAngle(-15, 0, 10));
  TrackedField<EulerAngle> leftArmPose = new TrackedField<>(new EulerAngle(-10, 0, 10));
  TrackedField<EulerAngle> rightLegPose = new TrackedField<>(new EulerAngle(1, 0, 1));
  TrackedField<EulerAngle> leftLegPose = new TrackedField<>(new EulerAngle(-1, 0, -1));

  public ClientArmorStand(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.ARMOR_STAND);
  }

  @Override
  public @NotNull EntityCategory getCategory() {
    return EntityCategory.NONE;
  }

  @NotNull
  public ItemStack getItemInHand() {
    return equipment.getItemInMainHand();
  }

  public void setItemInHand(@Nullable ItemStack item) {
    equipment.setItemInMainHand(item);
  }

  @NotNull
  public ItemStack getBoots() {
    return equipment.getBoots();
  }

  public void setBoots(@Nullable ItemStack item) {
    equipment.setBoots(item);
  }

  @NotNull
  public ItemStack getLeggings() {
    return equipment.getLeggings();
  }

  public void setLeggings(@Nullable ItemStack item) {
    equipment.setLeggings(item);
  }

  @NotNull
  public ItemStack getChestplate() {
    return equipment.getChestplate();
  }

  public void setChestplate(@Nullable ItemStack item) {
    equipment.setChestplate(item);
  }

  @NotNull
  public ItemStack getHelmet() {
    return equipment.getHelmet();
  }

  public boolean getArms() {
    return arms.getBooleanValue();
  }

  public void setHelmet(@Nullable ItemStack item) {
    equipment.setHelmet(item);
  }

  public EulerAngle getBodyPose() {
    return bodyPose.getValue();
  }

  public void setBodyPose(@NotNull EulerAngle pose) {
    setMeta(this.bodyPose, pose);
  }

  @NotNull
  @Override
  public EulerAngle getLeftArmPose() {
    return leftArmPose.getValue();
  }

  public void setLeftArmPose(@NotNull EulerAngle pose) {
    setMeta(this.leftArmPose, pose);
  }

  @NotNull
  @Override
  public EulerAngle getRightArmPose() {
    return rightArmPose.getValue();
  }

  public void setRightArmPose(@NotNull EulerAngle pose) {
    setMeta(this.rightArmPose, pose);
  }

  @NotNull
  @Override
  public EulerAngle getLeftLegPose() {
    return leftLegPose.getValue();
  }

  public void setLeftLegPose(@NotNull EulerAngle pose) {
    setMeta(this.leftLegPose, pose);
  }

  @NotNull
  @Override
  public EulerAngle getRightLegPose() {
    return rightLegPose.getValue();
  }

  public void setRightLegPose(@NotNull EulerAngle pose) {
    setMeta(this.rightLegPose, pose);
  }

  public @NotNull EulerAngle getHeadPose() {
    return headPose.getValue();
  }

  public void setHeadPose(@NotNull EulerAngle pose) {
    setMeta(this.headPose, pose);
  }

  public boolean hasArms() {
    return arms.getBooleanValue();
  }

  public void setArms(boolean arms) {
    setMeta(this.arms, arms);
  }

  @Override
  public boolean isSmall() {
    return small.getBooleanValue();
  }

  public void setSmall(boolean small) {
    setMeta(this.small, small);
  }

  @Override
  public boolean isMarker() {
    return marker.getBooleanValue();
  }

  public void setMarker(boolean marker) {
    setMeta(this.marker, marker);
  }

  @Override
  @Deprecated
  public void addEquipmentLock(@NotNull EquipmentSlot equipmentSlot, @NotNull ArmorStand.LockType lockType) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void removeEquipmentLock(@NotNull EquipmentSlot equipmentSlot, @NotNull ArmorStand.LockType lockType) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean hasEquipmentLock(@NotNull EquipmentSlot equipmentSlot, @NotNull ArmorStand.LockType lockType) {
    throw new ServerSideMethodNotSupported();
  }

  public boolean hasBasePlate() {
    return basePlate.getBooleanValue();
  }

  public void setBasePlate(boolean basePlate) {
    setMeta(this.basePlate, basePlate);
  }

  @Override
  public boolean isVisible() {
    return !invisible.getBooleanValue();
  }

  public void setVisible(boolean visible) {
    setMeta(this.invisible, !visible);
  }

  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (armorStandMask.hasChanged()) {
      data.add(new EntityData(15, EntityDataTypes.BYTE, armorStandMask.byteVal()));
    }
    if (headPose.hasChanged()) {
      data.add(new EntityData(16, EntityDataTypes.ROTATION,
          new Vector3f((float) headPose.getValue().getX(), (float) headPose.getValue().getY(), (float) headPose.getValue().getZ())));
    }
    if (bodyPose.hasChanged()) {
      data.add(new EntityData(17, EntityDataTypes.ROTATION,
          new Vector3f((float) bodyPose.getValue().getX(), (float) bodyPose.getValue().getY(), (float) bodyPose.getValue().getZ())));
    }
    if (leftArmPose.hasChanged()) {
      data.add(new EntityData(18, EntityDataTypes.ROTATION,
          new Vector3f((float) leftArmPose.getValue().getX(), (float) leftArmPose.getValue().getY(), (float) leftArmPose.getValue().getZ())));
    }
    if (rightArmPose.hasChanged()) {
      data.add(new EntityData(19, EntityDataTypes.ROTATION,
          new Vector3f((float) rightArmPose.getValue().getX(), (float) rightArmPose.getValue().getY(), (float) rightArmPose.getValue().getZ())));
    }
    if (leftLegPose.hasChanged()) {
      data.add(new EntityData(20, EntityDataTypes.ROTATION,
          new Vector3f((float) leftLegPose.getValue().getX(), (float) leftLegPose.getValue().getY(), (float) leftLegPose.getValue().getZ())));
    }
    if (rightLegPose.hasChanged()) {
      data.add(new EntityData(21, EntityDataTypes.ROTATION,
          new Vector3f((float) rightLegPose.getValue().getX(), (float) rightLegPose.getValue().getY(), (float) rightLegPose.getValue().getZ())));
    }
    return data;
  }
}
