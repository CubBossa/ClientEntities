package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

public class ClientSnowball extends ClientThrownItemProjectile implements Snowball {

  public ClientSnowball(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.SNOWBALL, new ItemStack(Material.SNOWBALL));
  }
}
