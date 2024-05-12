package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedField;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.NotNull;

public class ClientFallingBlock extends ClientEntity implements FallingBlock {

  TrackedField<BlockData> blockData = new TrackedField<>(Material.SAND.createBlockData());

  public ClientFallingBlock(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.FALLING_BLOCK);
  }

  @Override
  int data() {
    return SpigotConversionUtil.fromBukkitBlockData(blockData.getValue()).getGlobalId();
  }

  public void setBlockData(BlockData blockData) {
    setMeta(this.blockData, blockData);
  }

  @NotNull
  public Material getMaterial() {
    return blockData.getValue().getMaterial();
  }

  @NotNull
  @Override
  public BlockData getBlockData() {
    return blockData.getValue();
  }

  @Override  @Deprecated
  public boolean getDropItem() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setDropItem(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean getCancelDrop() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setCancelDrop(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean canHurtEntities() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setHurtEntities(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public float getDamagePerBlock() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setDamagePerBlock(float v) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public int getMaxDamage() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setMaxDamage(int i) {
    throw new ServerSideMethodNotSupported();
  }
}
