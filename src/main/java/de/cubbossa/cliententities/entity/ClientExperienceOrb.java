package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnExperienceOrb;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ClientExperienceOrb extends ClientEntity {

  int count;

  public ClientExperienceOrb(PlayerSpaceImpl playerSpace, int entityId, Location location, int count) {
    super(playerSpace, entityId, EntityType.EXPERIENCE_ORB);
    this.location = location;
    this.count = count;
  }

  @Override
  void spawn(Player player) {
    PacketEvents.getAPI().getPlayerManager().sendPacket(player,
        new WrapperPlayServerSpawnExperienceOrb(entityId, location.getX(), location.getY(), location.getZ(), (short) count));
  }
}
