package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ClientEnderPearl extends ClientThrownItemProjectile implements EnderPearl {

    public ClientEnderPearl(PlayerSpaceImpl playerSpace, int entityId) {
        super(playerSpace, entityId, EntityType.ENDER_PEARL, new ItemStack(Material.ENDER_PEARL));
    }
}
