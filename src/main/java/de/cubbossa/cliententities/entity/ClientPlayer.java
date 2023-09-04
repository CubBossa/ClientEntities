package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpace;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Getter
public class ClientPlayer extends ClientLivingEntity {

  boolean sneaking = false;

  MainHand mainHand = MainHand.RIGHT;
  boolean capeEnabled = true;
  boolean jacketEnabled = true;
  boolean rightSleeveEnabled = true;
  boolean leftSleeveEnabled = true;
  boolean leftPantsEnabled = true;
  boolean rightPantsEnabled = true;
  boolean hatEnabled = true;

  @Nullable Entity leftShoulderEntity = null;
  @Nullable Entity rightShoulderEntity = null;

  public ClientPlayer(PlayerSpace playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.PLAYER);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    byte skinMask = (byte) (
        (capeEnabled ? 0x1 : 0)
            | (jacketEnabled ? 0x2 : 0)
            | (leftSleeveEnabled ? 0x4 : 0)
            | (rightSleeveEnabled ? 0x8 : 0)
            | (leftPantsEnabled ? 0x10 : 0)
            | (rightPantsEnabled ? 0x20 : 0)
            | (hatEnabled ? 0x40 : 0)
    );
    if (skinMask > 0) {
      data.add(new EntityData(17, EntityDataTypes.BYTE, skinMask));
    }
    if (mainHand == MainHand.LEFT) {
      data.add(new EntityData(18, EntityDataTypes.BYTE, 1));
    }
    return data;
  }

  public void setMainHand(MainHand mainHand) {
    this.mainHand = setMeta(this.mainHand, mainHand);
  }

  @NotNull
  public MainHand getMainHand() {
    return MainHand.RIGHT;
  }

  @NotNull
  public ItemStack getItemInHand() {
    return equipment.getItemInHand();
  }

  public void setItemInHand(@Nullable ItemStack itemStack) {
    equipment.setItemInHand(itemStack);
  }


  public boolean isBlocking() {
    return isHandActive && !activeHandMainHand;
  }

  public boolean isHandRaised() {
    return isHandActive;
  }

  @Nullable
  public ItemStack getItemInUse() {
    return isHandActive ? null : equipment.getItemInMainHand();
  }

  public Entity getShoulderEntityLeft() {
    return leftShoulderEntity;
  }

  public void setShoulderEntityLeft(@Nullable Entity entity) {
    this.leftShoulderEntity = setMeta(this.leftShoulderEntity, leftShoulderEntity);
  }

  @Nullable
  public Entity getShoulderEntityRight() {
    return rightShoulderEntity;
  }

  public void setShoulderEntityRight(@Nullable Entity entity) {
    this.rightShoulderEntity = setMeta(this.rightShoulderEntity, rightShoulderEntity);
  }

  @Nullable
  public Firework fireworkBoost(@NotNull ItemStack itemStack) {
    return null;
  }

  @NotNull
  public PlayerProfile getPlayerProfile() {
    return null;
  }

  @NotNull
  public String getDisplayName() {
    return null;
  }

  public void setDisplayName(@Nullable String s) {

  }

  @NotNull
  public String getPlayerListName() {
    return null;
  }

  public void setPlayerListName(@Nullable String s) {

  }

  public boolean isSneaking() {
    return pose == Pose.SNEAKING;
  }

  public void setSneaking(boolean b) {
    setPose(Pose.SNEAKING);
  }

  @Override
  public @NotNull EntityCategory getCategory() {
    return EntityCategory.NONE;
  }

  public void setCapeEnabled(boolean capeEnabled) {
    this.capeEnabled = setMeta(this.capeEnabled, capeEnabled);
  }

  public void setJacketEnabled(boolean jacketEnabled) {
    this.jacketEnabled = setMeta(this.jacketEnabled, jacketEnabled);
  }

  public void setLeftSleeveEnabled(boolean leftSleeveEnabled) {
    this.leftSleeveEnabled = setMeta(this.leftSleeveEnabled, leftSleeveEnabled);
  }

  public void setRightSleeveEnabled(boolean rightSleeveEnabled) {
    this.rightSleeveEnabled = setMeta(this.rightSleeveEnabled, rightSleeveEnabled);
  }

  public void setLeftPantsEnabled(boolean leftPantsEnabled) {
    this.leftPantsEnabled = setMeta(this.leftPantsEnabled, leftPantsEnabled);
  }

  public void setRightPantsEnabled(boolean rightPantsEnabled) {
    this.rightPantsEnabled = setMeta(this.rightPantsEnabled, rightPantsEnabled);
  }

  public void setHatEnabled(boolean hatEnabled) {
    this.hatEnabled = setMeta(this.hatEnabled, hatEnabled);
  }
}
