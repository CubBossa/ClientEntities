package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import lombok.Getter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class ClientThrownItemProjectile extends ClientEntity {

  ItemStack item;

  public ClientThrownItemProjectile(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType, ItemStack stack) {
    super(playerSpace, entityId, entityType);
    this.item = stack;
  }

  public void setItem(@NotNull ItemStack itemStack) {
    this.item = setMeta(this.item, itemStack);
  }
}
