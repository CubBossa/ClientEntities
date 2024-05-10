package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;
import org.jetbrains.annotations.NotNull;

public class ClientSquid extends ClientMob implements Squid {

  public ClientSquid(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.SQUID);
  }

  @NotNull
  @Override
  public EntityCategory getCategory() {
    return EntityCategory.WATER;
  }
}
