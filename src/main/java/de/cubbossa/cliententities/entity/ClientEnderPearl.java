package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.inventory.ItemStack;

public class ClientEnderPearl extends ClientThrownItemProjectile implements EnderPearl {

    public ClientEnderPearl(PlayerSpaceImpl playerSpace, int entityId) {
        super(playerSpace, entityId, EntityTypes.ENDER_PEARL, new ItemStack(Material.ENDER_PEARL));
    }
}
