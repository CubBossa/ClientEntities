package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClientBlockDisplay extends ClientDisplay implements BlockDisplay {

  TrackedField<BlockData> block = new TrackedField<>(Material.LIME_CONCRETE.createBlockData());

  public ClientBlockDisplay(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.BLOCK_DISPLAY);
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
    data.add(new EntityData(22, EntityDataTypes.BLOCK_STATE, SpigotConversionUtil.fromBukkitBlockData(block.getValue()).getGlobalId()));
    return data;
  }
}
