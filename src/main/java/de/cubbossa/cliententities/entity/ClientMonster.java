package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.entity.Creature;

public abstract class ClientMonster extends ClientCreature implements Creature {

  public ClientMonster(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }
}
