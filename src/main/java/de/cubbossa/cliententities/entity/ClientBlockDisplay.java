package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
import de.cubbossa.cliententities.entitydata.BlockDisplayDataWrapper;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.NotNull;

public class ClientBlockDisplay extends ClientDisplay implements BlockDisplay {

  TrackedField<BlockData> block = new TrackedField<>(Material.MAGENTA_CONCRETE.createBlockData());

  public ClientBlockDisplay(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.BLOCK_DISPLAY);
  }

  @NotNull
  @Override
  public BlockData getBlock() {
    return block.getValue();
  }

  public void setBlock(@NotNull BlockData block) {
    setMeta(this.block, block);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (block.hasChanged()) {
      data.add(BlockDisplayDataWrapper.block(SpigotConversionUtil.fromBukkitBlockData(block.getValue())));
      block.flushChanged();
    }
    return data;
  }
}
