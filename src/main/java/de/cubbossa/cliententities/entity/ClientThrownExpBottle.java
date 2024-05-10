package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.inventory.ItemStack;

public class ClientThrownExpBottle extends ClientThrownItemProjectile implements ThrownExpBottle {

    public ClientThrownExpBottle(PlayerSpaceImpl playerSpace, int entityId) {
        super(playerSpace, entityId, EntityTypes.EXPERIENCE_BOTTLE, new ItemStack(Material.EXPERIENCE_BOTTLE));
    }
}
