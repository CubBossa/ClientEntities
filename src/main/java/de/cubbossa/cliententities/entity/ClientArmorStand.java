package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import lombok.Getter;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class ClientArmorStand extends ClientLivingEntity {

  private boolean small = false;
  private boolean basePlate = true;
  private boolean marker = false;
  private boolean arms = false;

  private EulerAngle headPose = new EulerAngle(0, 0, 0);
  private EulerAngle bodyPose = new EulerAngle(0, 0, 0);
  private EulerAngle rightArmPose = new EulerAngle(0, 0, 0);
  private EulerAngle leftArmPose = new EulerAngle(0, 0, 0);
  private EulerAngle rightLegPose = new EulerAngle(0, 0, 0);
  private EulerAngle leftLegPose = new EulerAngle(0, 0, 0);

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

  public void setHelmet(@Nullable ItemStack item) {
    equipment.setHelmet(item);
  }

  public void setBodyPose(@NotNull EulerAngle pose) {
    this.bodyPose = setMeta(this.bodyPose, pose);
  }

  public void setLeftArmPose(@NotNull EulerAngle pose) {
    this.leftArmPose = setMeta(this.leftArmPose, pose);
  }

  public void setRightArmPose(@NotNull EulerAngle pose) {
    this.rightArmPose = setMeta(this.rightArmPose, pose);
  }

  public void setLeftLegPose(@NotNull EulerAngle pose) {
    this.leftLegPose = setMeta(this.leftLegPose, pose);
  }

  public void setRightLegPose(@NotNull EulerAngle pose) {
    this.rightLegPose = setMeta(this.rightLegPose, pose);
  }

  public void setHeadPose(@NotNull EulerAngle pose) {
    this.headPose = setMeta(this.headPose, pose);
  }

  public boolean hasArms() {
    return arms;
  }

  public void setArms(boolean arms) {
    this.arms = setMeta(this.arms, arms);
  }

  public void setSmall(boolean small) {
    this.small = setMeta(this.small, small);
  }

  public void setMarker(boolean marker) {
    this.marker = setMeta(this.marker, marker);
  }

  public boolean hasBasePlate() {
    return basePlate;
  }

  public void setBasePlate(boolean basePlate) {
    this.basePlate = setMeta(this.basePlate, basePlate);
  }

  public void setVisible(boolean visible) {
    this.visible = setMeta(this.visible, visible);
  }

  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    byte mask = (byte)
        ((isSmall() ? 0x01 : 0)
            | (hasArms() ? 0x02 : 0)
            | (!hasBasePlate() ? 0x04 : 0)
            | (isMarker() ? 0x08 : 0));
    if (mask != 0) {
      data.add(new EntityData(15, EntityDataTypes.BYTE, mask));
    }
    if (!headPose.equals(new EulerAngle(0, 0, 0))) {
      data.add(new EntityData(16, EntityDataTypes.ROTATION,
          new Vector3f((float) headPose.getX(), (float) headPose.getY(), (float) headPose.getZ())));
    }
    if (!bodyPose.equals(new EulerAngle(0, 0, 0))) {
      data.add(new EntityData(17, EntityDataTypes.ROTATION,
          new Vector3f((float) bodyPose.getX(), (float) bodyPose.getY(), (float) bodyPose.getZ())));
    }
    if (!leftArmPose.equals(new EulerAngle(-10, 0, 10))) {
      data.add(new EntityData(18, EntityDataTypes.ROTATION,
          new Vector3f((float) leftArmPose.getX(), (float) leftArmPose.getY(), (float) leftArmPose.getZ())));
    }
    if (!rightArmPose.equals(new EulerAngle(-15, 0, 10))) {
      data.add(new EntityData(19, EntityDataTypes.ROTATION,
          new Vector3f((float) rightArmPose.getX(), (float) rightArmPose.getY(), (float) rightArmPose.getZ())));
    }
    if (!leftLegPose.equals(new EulerAngle(-1, 0, -1))) {
      data.add(new EntityData(20, EntityDataTypes.ROTATION,
          new Vector3f((float) leftLegPose.getX(), (float) leftLegPose.getY(), (float) leftLegPose.getZ())));
    }
    if (!rightLegPose.equals(new EulerAngle(1, 0, 1))) {
      data.add(new EntityData(21, EntityDataTypes.ROTATION,
          new Vector3f((float) rightLegPose.getX(), (float) rightLegPose.getY(), (float) rightLegPose.getZ())));
    }
    return data;
  }
}
