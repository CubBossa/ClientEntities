package de.cubbossa.cliententities.entity;

import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ClientDamageable extends ClientEntity {

  private double maxHealth;
  private double health;

  public ClientDamageable(PlayerSpace playerSpace, int entityId, EntityType entityType) {
    super(playerSpace, entityId, entityType);
  }

  public void damage(double amount) {
    health -= amount;
    if (health <= 0) {
      // TODO death animation
      remove();
    }
  }

  public void damage(double amount, @Nullable Entity source) {
    damage(amount);
  }

  public void resetMaxHealth() {
    this.health = this.maxHealth;
  }
}
