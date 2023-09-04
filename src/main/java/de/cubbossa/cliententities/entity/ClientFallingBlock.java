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
public class ClientFallingBlock extends ClientEntity {

  BlockData blockData = Material.SAND.createBlockData();

  public ClientFallingBlock(PlayerSpace playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.FALLING_BLOCK);
  }

  @Override
  int data() {
    return SpigotConversionUtil.fromBukkitBlockData(blockData).getGlobalId();
  }

  public void setBlockData(BlockData blockData) {
    this.blockData = setMeta(this.blockData, blockData);
  }

  @NotNull
  public Material getMaterial() {
    return blockData.getMaterial();
  }
}
