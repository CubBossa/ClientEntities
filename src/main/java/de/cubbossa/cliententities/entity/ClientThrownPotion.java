package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClientThrownPotion extends ClientThrownItemProjectile implements ThrownPotion {

    public ClientThrownPotion(PlayerSpace playerSpace, int entityId) {
        super(playerSpace, entityId, EntityType.SPLASH_POTION, new ItemStack(Material.SPLASH_POTION));
    }

    @NotNull
    @Override
    public Collection<PotionEffect> getEffects() {
        throw new ClientEntityMethodNotSupportedException();
    }
}
