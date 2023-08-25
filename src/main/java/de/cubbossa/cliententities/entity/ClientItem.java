package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Getter
public class ClientItem extends ClientEntity implements Item {

  ItemStack itemStack;

  public ClientItem(PlayerSpace playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.DROPPED_ITEM);
  }

  @Override
  public void setItemStack(@NotNull ItemStack itemStack) {
    this.itemStack = setMeta(this.itemStack, itemStack);
  }

  @Override
  public int getPickupDelay() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setPickupDelay(int i) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setUnlimitedLifetime(boolean b) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean isUnlimitedLifetime() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setOwner(@Nullable UUID uuid) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public UUID getOwner() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public void setThrower(@Nullable UUID uuid) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Nullable
  @Override
  public UUID getThrower() {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    data.add(new EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack)));
    return data;
  }
}
