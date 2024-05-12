package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LeashHitch;
import org.jetbrains.annotations.NotNull;

public class ClientLeashHitch extends ClientEntity implements LeashHitch {

  public ClientLeashHitch(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.LEASH_KNOT);
  }

  @Override
  public boolean setFacingDirection(@NotNull BlockFace blockFace, boolean b) {
    return true;
  }

  @NotNull
  @Override
  public BlockFace getAttachedFace() {
    return BlockFace.SELF;
  }

  @Override
  public void setFacingDirection(@NotNull BlockFace blockFace) {
  }
}
