package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class ClientItem extends ClientEntity {

  ItemStack itemStack;

  public ClientItem(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.DROPPED_ITEM);
  }

  public void setItemStack(@NotNull ItemStack itemStack) {
    this.itemStack = setMeta(this.itemStack, itemStack);
  }

  public void pickup(ClientEntity entity) {
    //TODO
  }

  public void pickup(Entity entity) {

  }

  private void pickup(int entity) {

  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    data.add(new EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack)));
    return data;
  }
}
