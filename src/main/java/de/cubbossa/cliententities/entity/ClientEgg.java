package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ClientEgg extends ClientThrownItemProjectile {

  public ClientEgg(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.EGG, new ItemStack(Material.EGG));
  }
}
