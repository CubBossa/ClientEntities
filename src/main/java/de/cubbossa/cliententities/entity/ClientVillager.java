package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.villager.VillagerData;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.ServerSideMethodNotSupported;
import de.cubbossa.cliententities.TrackedField;
import lombok.Getter;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientVillager extends ClientAbstractVillager implements Villager {

    TrackedField<Villager.Type> villagerType = new TrackedField<>(Villager.Type.PLAINS);
    TrackedField<Villager.Profession> profession = new TrackedField<>(Villager.Profession.NONE);
    TrackedField<Integer> villagerLevel = new TrackedField<>(0);

    public ClientVillager(PlayerSpaceImpl playerSpace, int entityId) {
        super(playerSpace, entityId, EntityType.VILLAGER);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (villagerLevel.hasChanged() || villagerType.hasChanged() || profession.hasChanged()) {
            data.add(new EntityData(18, EntityDataTypes.VILLAGER_DATA, new VillagerData(
                    villagerType.getValue().ordinal(), profession.getValue().ordinal(), villagerLevel.getValue()
            )));
        }
        return data;
    }

    @NotNull
    @Override
    public Profession getProfession() {
        return profession.getValue();
    }

    public void setProfession(@NotNull Villager.Profession profession) {
    setMeta(this.profession, profession);
    }

    @NotNull
    @Override
    public Type getVillagerType() {
        return villagerType.getValue();
    }

    public void setVillagerType(@NotNull Villager.Type type) {
    setMeta(this.villagerType, type);
    }

    @Override
    public int getVillagerLevel() {
        return villagerLevel.getValue();
    }

    public void setVillagerLevel(int i) {
    setMeta(this.villagerLevel, i);
    }

    @Override
    public int getVillagerExperience() {
        throw new ServerSideMethodNotSupported();
    }

    @Override
    public void setVillagerExperience(int i) {
        throw new ServerSideMethodNotSupported();
    }

    public boolean sleep(@NotNull Location location) {
        teleport(location);
        pose.setValue(Pose.SLEEPING);
        return true;
    }

    public void wakeup() {
        pose.setValue(Pose.STANDING);
    }

    public void shakeHead() {
        setMeta(this.headShakeTimer, 40);
        this.headShakeTimer.overrideChanged(true);
    }

    @Nullable
    @Override
    public ZombieVillager zombify() {
        throw new ServerSideMethodNotSupported();
    }

    public void shakeHead(int ticks) {
        setMeta(this.headShakeTimer, ticks);
        this.headShakeTimer.overrideChanged(true);
    }

    public void happy() {
        playEffect(EntityEffect.VILLAGER_HAPPY);
    }

    public void angry() {
        playEffect(EntityEffect.VILLAGER_ANGRY);
    }

    public void hearts() {
        playEffect(EntityEffect.VILLAGER_HEART);
    }

    public void levelUp() {
        playEffect(EntityEffect.VILLAGER_SPLASH);
    }

    public void playSound(Sound sound) {
        playSound(sound, SoundCategory.NEUTRAL);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        playSound(sound, SoundCategory.NEUTRAL, volume, pitch);
    }
}
