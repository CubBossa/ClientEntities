package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@Getter
public class ClientFireWork extends ClientEntity {

  ItemStack fireWorkInfo = null;
  LivingEntity attachedTo = null;
  ProjectileSource projectileSource = null;
  boolean shotAtAngle = false;
  boolean detonated = false;

  public ClientFireWork(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.FIREWORK);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (fireWorkInfo != null) {
      data.add(new EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(fireWorkInfo)));
    }
    if (attachedTo != null) {
      data.add(new EntityData(9, EntityDataTypes.OPTIONAL_INT, Optional.of(attachedTo.getEntityId())));
    }
    if (shotAtAngle) {
      data.add(new EntityData(10, EntityDataTypes.BOOLEAN, true));
    }
    return data;
  }

  @NotNull
  public FireworkMeta getFireworkMeta() {
    if (fireWorkInfo == null) {
      fireWorkInfo = new ItemStack(Material.FIREWORK_ROCKET);
    }
    if (fireWorkInfo.getType() != Material.FIREWORK_ROCKET) {
      return (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
    }
    FireworkMeta meta = (FireworkMeta) fireWorkInfo.getItemMeta();
    if (meta == null) {
      meta = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
    }
    return meta.clone();
  }

  public void setFireworkMeta(@NotNull FireworkMeta fireworkMeta) {
    if (fireWorkInfo == null) {
      fireWorkInfo = new ItemStack(Material.FIREWORK_ROCKET);
    }
    if (fireWorkInfo.getType() != Material.FIREWORK_ROCKET) {
      fireWorkInfo.setType(Material.FIREWORK_ROCKET);
    }
    if (!this.fireWorkInfo.setItemMeta(fireworkMeta)) {
      throw new IllegalStateException("Could not set firework meta");
    }
  }

  public boolean setAttachedTo(@Nullable LivingEntity livingEntity) {
    this.attachedTo = setMeta(this.attachedTo, livingEntity);
    return true;
  }

  public void detonate() {
    playEffect(EntityEffect.FIREWORK_EXPLODE);
    detonated = true;
  }

  public void setShotAtAngle(boolean b) {
    this.shotAtAngle = setMeta(this.shotAtAngle, b);
  }
}
