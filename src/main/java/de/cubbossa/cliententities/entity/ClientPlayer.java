package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import de.cubbossa.cliententities.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ClientPlayer extends ClientLivingEntity implements HumanEntity {

  TrackedField<Integer> pingDisplay = new TrackedField<>(0);
  TrackedBoolField displayInTab = new TrackedBoolField();
  TrackedField<GameMode> gameModeDisplay = new TrackedField<>(GameMode.CREATIVE);
  TrackedField<String> name = new TrackedField<>("");
  TrackedField<Component> displayName = new TrackedField<>(Component.empty());
  TrackedField<Component> tabName = new TrackedField<>(Component.empty());

  TrackedField<MainHand> mainHand = new TrackedField<>(MainHand.RIGHT);
  TrackedBoolField capeEnabled = new TrackedBoolField(true);
  TrackedBoolField jacketEnabled = new TrackedBoolField(true);
  TrackedBoolField rightSleeveEnabled = new TrackedBoolField(true);
  TrackedBoolField leftSleeveEnabled = new TrackedBoolField(true);
  TrackedBoolField leftPantsEnabled = new TrackedBoolField(true);
  TrackedBoolField rightPantsEnabled = new TrackedBoolField(true);
  TrackedBoolField hatEnabled = new TrackedBoolField(true);
  TrackedMask skinMask = new TrackedMask(
      capeEnabled, jacketEnabled, rightSleeveEnabled, leftSleeveEnabled, rightPantsEnabled, leftPantsEnabled, hatEnabled
  );

  TrackedField<@Nullable Entity> leftShoulderEntity = new TrackedField<>();
  TrackedField<@Nullable Entity> rightShoulderEntity = new TrackedField<>();

  public ClientPlayer(PlayerSpaceImpl playerSpace, String name) {
    super(playerSpace, -1, EntityTypes.PLAYER);
    this.name.setValue(name);
    this.displayName.setValue(Component.text(name));
  }

  @Override
  List<PacketWrapper<?>> spawnPacket() {
    WrapperPlayServerPlayerInfoUpdate.PlayerInfo data = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
        new UserProfile(uniqueId, name.getValue()),
        displayInTab.getBooleanValue(),
        pingDisplay.getValue(),
        SpigotConversionUtil.fromBukkitGameMode(gameModeDisplay.getValue()),
        tabName.getValue(),
        null
    );
    return List.of(
        new WrapperPlayServerPlayerInfoUpdate(EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER), data),
        new WrapperPlayServerSpawnPlayer(entityId, uniqueId, SpigotConversionUtil.fromBukkitLocation(location), metaData())
    );
  }

  @Override
  public List<UpdateInfo> state(boolean onlyIfChanged) {
    List<UpdateInfo> info = super.state(onlyIfChanged);

    if (displayInTab.hasChanged() || pingDisplay.hasChanged() || gameModeDisplay.hasChanged() || tabName.hasChanged()) {
      WrapperPlayServerPlayerInfoUpdate.PlayerInfo data = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
          new UserProfile(uniqueId, name.getValue()),
          displayInTab.getBooleanValue(),
          pingDisplay.getValue(),
          SpigotConversionUtil.fromBukkitGameMode(gameModeDisplay.getValue()),
          tabName.getValue(),
          null
      );
      info.add(PacketInfo.packet(
          new WrapperPlayServerPlayerInfoUpdate(EnumSet.of(
              WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
              WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
              WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
              WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME
          ), data)
      ));
    }

    return info;
  }

  public void setDisplayInTab(boolean displayInTab) {
    this.displayInTab.setValue(displayInTab);
  }

  public void setGameModeDisplay(GameMode gameModeDisplay) {
    this.gameModeDisplay.setValue(gameModeDisplay);
  }

  public void setPingDisplay(int pingDisplay) {
    this.pingDisplay.setValue(pingDisplay);
  }

  public void setDisplayName(Component displayName) {
    this.displayName.setValue(displayName);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (skinMask.hasChanged()) {
      data.add(new EntityData(17, EntityDataTypes.BYTE, skinMask.byteVal()));
    }
    if (mainHand.hasChanged()) {
      data.add(new EntityData(18, EntityDataTypes.BYTE, mainHand.getValue() == MainHand.LEFT
          ? 1 : 0));
    }
    return data;
  }

  public void setMainHand(MainHand mainHand) {
    setMeta(this.mainHand, mainHand);
  }

  @NotNull
  @Override
  @Deprecated
  public PlayerInventory getInventory() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public Inventory getEnderChest() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  public MainHand getMainHand() {
    return mainHand.getValue();
  }

  @Override
  @Deprecated
  public boolean setWindowProperty(@NotNull InventoryView.Property property, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getEnchantmentSeed() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setEnchantmentSeed(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public InventoryView getOpenInventory() {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public InventoryView openInventory(@NotNull Inventory inventory) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public InventoryView openWorkbench(@Nullable Location location, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public InventoryView openEnchanting(@Nullable Location location, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void openInventory(@NotNull InventoryView inventoryView) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public InventoryView openMerchant(@NotNull Villager villager, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public InventoryView openMerchant(@NotNull Merchant merchant, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void closeInventory() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  public ItemStack getItemInHand() {
    return equipment.getItemInHand();
  }

  public void setItemInHand(@Nullable ItemStack itemStack) {
    equipment.setItemInHand(itemStack);
  }

  @NotNull
  @Override
  @Deprecated
  public ItemStack getItemOnCursor() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setItemOnCursor(@Nullable ItemStack itemStack) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean hasCooldown(@NotNull Material material) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getCooldown(@NotNull Material material) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setCooldown(@NotNull Material material, int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getSleepTicks() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean sleep(@NotNull Location location, boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void wakeup(boolean b) {

  }

  @NotNull
  @Override
  public GameMode getGameMode() {
    return gameModeDisplay.getValue();
  }

  @Override
  public void setGameMode(@NotNull GameMode gameMode) {
    this.gameModeDisplay.setValue(gameMode);
  }


  public boolean isBlocking() {
    return isHandActive.getBooleanValue() && !activeHandMainHand.getBooleanValue();
  }

  public boolean isHandRaised() {
    return isHandActive.getBooleanValue();
  }

  @Nullable
  public ItemStack getItemInUse() {
    return isHandActive.getBooleanValue() ? null : equipment.getItemInMainHand();
  }

  @Override
  @Deprecated
  public int getExpToLevel() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public float getAttackCooldown() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean discoverRecipe(@NotNull NamespacedKey namespacedKey) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int discoverRecipes(@NotNull Collection<NamespacedKey> collection) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean undiscoverRecipe(@NotNull NamespacedKey namespacedKey) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int undiscoverRecipes(@NotNull Collection<NamespacedKey> collection) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean hasDiscoveredRecipe(@NotNull NamespacedKey namespacedKey) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  @Deprecated
  public Set<NamespacedKey> getDiscoveredRecipes() {
    throw new ServerSideMethodNotSupported();
  }

  public Entity getShoulderEntityLeft() {
    return leftShoulderEntity.getValue();
  }

  public void setShoulderEntityLeft(@Nullable Entity entity) {
    setMeta(this.leftShoulderEntity, entity);
  }

  @Nullable
  public Entity getShoulderEntityRight() {
    return rightShoulderEntity.getValue();
  }

  public void setShoulderEntityRight(@Nullable Entity entity) {
    setMeta(this.rightShoulderEntity, entity);
  }

  @Override
  @Deprecated
  public boolean dropItem(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public float getExhaustion() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setExhaustion(float v) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public float getSaturation() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setSaturation(float v) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getFoodLevel() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setFoodLevel(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getSaturatedRegenRate() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setSaturatedRegenRate(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getUnsaturatedRegenRate() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setUnsaturatedRegenRate(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public int getStarvationRate() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setStarvationRate(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public Location getLastDeathLocation() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setLastDeathLocation(@Nullable Location location) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Deprecated
  public Firework fireworkBoost(@NotNull ItemStack itemStack) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Deprecated
  public PlayerProfile getPlayerProfile() {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Deprecated
  public String getDisplayName() {
    throw new ServerSideMethodNotSupported();
  }

  @Deprecated
  public void setDisplayName(@Nullable String s) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Deprecated
  public String getPlayerListName() {
    throw new ServerSideMethodNotSupported();
  }

  @Deprecated
  public void setPlayerListName(@Nullable String s) {
    throw new ServerSideMethodNotSupported();
  }

  public boolean isSneaking() {
    return pose.getValue() == Pose.SNEAKING;
  }

  public void setSneaking(boolean b) {
    pose.setValue(Pose.SNEAKING);
  }

  @Override
  public @NotNull EntityCategory getCategory() {
    return EntityCategory.NONE;
  }

  public void setCapeEnabled(boolean capeEnabled) {
    setMeta(this.capeEnabled, capeEnabled);
  }

  public void setJacketEnabled(boolean jacketEnabled) {
    setMeta(this.jacketEnabled, jacketEnabled);
  }

  public void setLeftSleeveEnabled(boolean leftSleeveEnabled) {
    setMeta(this.leftSleeveEnabled, leftSleeveEnabled);
  }

  public void setRightSleeveEnabled(boolean rightSleeveEnabled) {
    setMeta(this.rightSleeveEnabled, rightSleeveEnabled);
  }

  public void setLeftPantsEnabled(boolean leftPantsEnabled) {
    setMeta(this.leftPantsEnabled, leftPantsEnabled);
  }

  public void setRightPantsEnabled(boolean rightPantsEnabled) {
    setMeta(this.rightPantsEnabled, rightPantsEnabled);
  }

  public void setHatEnabled(boolean hatEnabled) {
    setMeta(this.hatEnabled, hatEnabled);
  }
}
