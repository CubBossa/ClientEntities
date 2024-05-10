package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedField;
import lombok.Getter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientThrownItemProjectile extends ClientEntity implements ThrowableProjectile {

  TrackedField<ItemStack> item = new TrackedField<>();

  public ClientThrownItemProjectile(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType, ItemStack stack) {
    super(playerSpace, entityId, entityType);
    this.item.setValue(stack);
  }

  @NotNull
  @Override
  public ItemStack getItem() {
    return item.getValue();
  }

  public void setItem(@NotNull ItemStack itemStack) {
    setMeta(this.item, itemStack);
  }

  @Nullable
  @Override  @Deprecated
  public ProjectileSource getShooter() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setShooter(@Nullable ProjectileSource projectileSource) {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public boolean doesBounce() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setBounce(boolean b) {
    throw new ServerSideMethodNotSupported();
  }
}
