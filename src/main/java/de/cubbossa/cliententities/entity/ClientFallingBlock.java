package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.NotNull;

@Getter
public class ClientFallingBlock extends ClientEntity implements FallingBlock {

  BlockData blockData = Material.SAND.createBlockData();

  public ClientFallingBlock(PlayerSpace playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.FALLING_BLOCK);
  }

  @Override
  int data() {
    return SpigotConversionUtil.fromBukkitBlockData(blockData).getGlobalId();
  }

  @NotNull
  @Override
  public Material getMaterial() {
    return blockData.getMaterial();
  }

  @Override
  public boolean getDropItem() {
    return false;
  }

  @Override
  public void setDropItem(boolean b) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean getCancelDrop() {
    return true;
  }

  @Override
  public void setCancelDrop(boolean b) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public boolean canHurtEntities() {
    return false;
  }

  @Override
  public void setHurtEntities(boolean b) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public float getDamagePerBlock() {
    return 0;
  }

  @Override
  public void setDamagePerBlock(float v) {
    throw new ClientEntityMethodNotSupportedException();
  }

  @Override
  public int getMaxDamage() {
    return 0;
  }

  @Override
  public void setMaxDamage(int i) {
    throw new ClientEntityMethodNotSupportedException();
  }
}
