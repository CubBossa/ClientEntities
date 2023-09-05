package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.villager.VillagerData;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import lombok.Getter;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class ClientVillager extends ClientAbstractVillager {

    Villager.Type villagerType = Villager.Type.PLAINS;
    Villager.Profession profession = Villager.Profession.NONE;
    int villagerLevel = 0;

    public ClientVillager(PlayerSpaceImpl playerSpace, int entityId) {
        super(playerSpace, entityId, EntityType.VILLAGER);
    }

    @Override
    public @NotNull EntityCategory getCategory() {
        return EntityCategory.NONE;
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (villagerLevel != 0 || villagerType != Villager.Type.PLAINS || profession != Villager.Profession.NONE) {
            data.add(new EntityData(18, EntityDataTypes.VILLAGER_DATA, new VillagerData(
                    villagerType.ordinal(), profession.ordinal(), villagerLevel
            )));
        }
        return data;
    }

    public void setProfession(@NotNull Villager.Profession profession) {
        this.profession = setMeta(this.profession, profession);
    }

    public void setVillagerType(@NotNull Villager.Type type) {
        this.villagerType = setMeta(this.villagerType, type);
    }

    public void setVillagerLevel(int i) {
        this.villagerLevel = setMeta(this.villagerLevel, i);
    }

    public boolean sleep(@NotNull Location location) {
        teleport(location);
        setPose(Pose.SLEEPING);
        return true;
    }

    public void wakeup() {
        setPose(Pose.STANDING);
    }

    public void shakeHead() {
        this.headShakeTimer = setMeta(this.headShakeTimer, 40);
    }

    public void shakeHead(int ticks) {
        this.headShakeTimer = setMeta(this.headShakeTimer, ticks);
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
