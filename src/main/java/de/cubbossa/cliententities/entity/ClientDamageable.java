package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedField;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientDamageable extends ClientEntity implements Damageable {

  TrackedField<Double> maxHealth = new TrackedField<>(1d);
  TrackedField<Double> health = new TrackedField<>(1d);

  public ClientDamageable(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  public void damage(double amount) {
    health.setValue(health.getValue() - amount);
    if (health.getValue() <= 0) {
      remove();
    }
  }

  public void damage(double amount, @Nullable Entity source) {
    damage(amount);
  }

  @Override
  public void damage(double v, @NotNull DamageSource damageSource) {
    damage(v);
  }

  @Override
  public double getHealth() {
    return health.getValue();
  }

  @Override
  public void setHealth(double v) {
    health.setValue(v);
  }

  @Override  @Deprecated
  public double getAbsorptionAmount() {
    throw new ServerSideMethodNotSupported();
  }

  @Override  @Deprecated
  public void setAbsorptionAmount(double v) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public double getMaxHealth() {
    return maxHealth.getValue();
  }

  @Override
  public void setMaxHealth(double v) {
    maxHealth.setValue(v);
  }

  public void resetMaxHealth() {
    this.health = this.maxHealth;
  }
}
