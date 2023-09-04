package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpace;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

public class ClientSnowball extends ClientThrownItemProjectile {

  public ClientSnowball(PlayerSpace playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.SNOWBALL, new ItemStack(Material.SNOWBALL));
  }
}
