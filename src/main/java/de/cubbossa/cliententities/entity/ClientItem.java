package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedField;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientItem extends ClientEntity implements Item {

  TrackedField<ItemStack> itemStack = new TrackedField<>(new ItemStack(Material.AIR));

  public ClientItem(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.ITEM);
  }

  @NotNull
  @Override
  public ItemStack getItemStack() {
    return itemStack.getValue();
  }

  public void setItemStack(@NotNull ItemStack itemStack) {
    setMeta(this.itemStack, itemStack);
  }

  @Override
  @Deprecated
  public int getPickupDelay() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setPickupDelay(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setUnlimitedLifetime(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public boolean isUnlimitedLifetime() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setOwner(@Nullable UUID uuid) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public UUID getOwner() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  @Deprecated
  public void setThrower(@Nullable UUID uuid) {
    throw new ServerSideMethodNotSupported();
  }

  @Nullable
  @Override
  @Deprecated
  public UUID getThrower() {
    throw new ServerSideMethodNotSupported();
  }

  public void pickup(Entity entity) {
    pickup(entity.getEntityId());
  }

  private void pickup(int entity) {
    statelessEffect.add(() -> PacketInfo.packet(
        new WrapperPlayServerCollectItem(entityId, entity, getItemStack().getAmount()
        )));
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    data.add(new EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack.getValue())));
    return data;
  }
}
