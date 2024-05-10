package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.inventory.ItemStack;

public class ClientEgg extends ClientThrownItemProjectile implements Egg {

  public ClientEgg(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.EGG, new ItemStack(Material.EGG));
  }
}
