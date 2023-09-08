package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

public class ClientSnowball extends ClientThrownItemProjectile implements Snowball {

  public ClientSnowball(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.SNOWBALL, new ItemStack(Material.SNOWBALL));
  }
}
