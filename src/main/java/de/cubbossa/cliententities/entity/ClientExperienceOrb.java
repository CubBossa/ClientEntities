package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnExperienceOrb;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClientExperienceOrb extends ClientEntity implements ExperienceOrb {

  final int count;

  public ClientExperienceOrb(PlayerSpaceImpl playerSpace, int entityId, Location location, int count) {
    super(playerSpace, entityId, EntityType.EXPERIENCE_ORB);
    this.location = location;
    this.count = count;
  }

  @Override
  List<PacketWrapper<?>> spawnPacket() {
    return Collections.singletonList(new WrapperPlayServerSpawnExperienceOrb(entityId, location.getX(), location.getY(), location.getZ(), (short) count));
  }

  @Override
  public int getExperience() {
    return count;
  }

  @Override
  @Deprecated
  public void setExperience(int i) {
    throw new ServerSideMethodNotSupported();
  }
}
