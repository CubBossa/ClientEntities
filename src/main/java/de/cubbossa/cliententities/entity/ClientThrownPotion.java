package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

public class ClientThrownPotion extends ClientThrownItemProjectile {

    public ClientThrownPotion(PlayerSpaceImpl playerSpace, int entityId, PotionData potionEffect) {
        super(playerSpace, entityId, EntityType.SPLASH_POTION, null);

        ItemStack stack = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        if (meta == null) meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.SPLASH_POTION);
        meta.setBasePotionData(potionEffect);
        stack.setItemMeta(meta);
        setItem(stack);
    }
}
