package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class ClientBlockDisplay extends ClientDisplay {

  BlockData block = Material.LIME_CONCRETE.createBlockData();

  public ClientBlockDisplay(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.BLOCK_DISPLAY);
  }

  public void setBlock(@NotNull BlockData block) {
    this.block = setMeta(this.block, block);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    data.add(new EntityData(22, EntityDataTypes.BLOCK_STATE, SpigotConversionUtil.fromBukkitBlockData(block).getGlobalId()));
    return data;
  }
}
