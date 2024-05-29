package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.TrackedField;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientZombie extends ClientMonster implements Zombie {

  TrackedBoolField baby = new TrackedBoolField(false);
  TrackedBoolField villager = new TrackedBoolField(false);
  TrackedBoolField converting = new TrackedBoolField(false);
  TrackedField<Villager.Profession> profession = new TrackedField<>();

  public ClientZombie(PlayerSpaceImpl playerSpace, int entityId, boolean villager) {
    super(playerSpace, entityId, villager ? EntityTypes.ZOMBIE_VILLAGER : EntityTypes.ZOMBIE);
    setMeta(this.villager, villager);
  }

  @Override
  public boolean isBaby() {
    return baby.getBooleanValue();
  }

  @Override
  public void setBaby(boolean b) {
    setMeta(baby, b);
  }

  @Override
  public boolean isVillager() {
    return type.equals(EntityTypes.ZOMBIE_VILLAGER);
  }

  @Override
  public void setVillager(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setVillagerProfession(Villager.Profession profession) {
    if (isVillager()) {
      setMeta(this.profession, profession);
    }
  }

  @Nullable
  @Override
  public Villager.Profession getVillagerProfession() {
    return profession.getValue();
  }

  @Override
  public boolean isConverting() {
    return converting.getBooleanValue();
  }

  @Override
  public int getConversionTime() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setConversionTime(int i) {
    setMeta(converting, i > 0);
  }

  @Override
  public boolean canBreakDoors() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setCanBreakDoors(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public int getAge() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setAge(int i) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setAgeLock(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public boolean getAgeLock() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setBaby() {
    setMeta(baby, true);
  }

  @Override
  public void setAdult() {
    setMeta(baby, false);
  }

  @Override
  public boolean isAdult() {
    return !baby.getBooleanValue();
  }

  @Override
  public boolean canBreed() {
    throw new ServerSideMethodNotSupported();
  }

  @Override
  public void setBreed(boolean b) {
    throw new ServerSideMethodNotSupported();
  }

  @NotNull
  @Override
  public EntityCategory getCategory() {
    return EntityCategory.UNDEAD;
  }
}
