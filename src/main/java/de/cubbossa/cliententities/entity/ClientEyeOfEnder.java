package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.ClientEntityMethodNotSupportedException;
import de.cubbossa.cliententities.PlayerSpace;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class ClientEyeOfEnder extends ClientEntity {

    Location targetLocation = location;
    ItemStack item = null;

    public ClientEyeOfEnder(PlayerSpace playerSpace, int entityId) {
        super(playerSpace, entityId, EntityType.ENDER_SIGNAL);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (item != null) {
            data.add(new EntityData(8, EntityDataTypes.ITEMSTACK, item));
        }
        return data;
    }

    public void setItem(ItemStack item) {
        this.item = setMeta(this.item, item);
    }

    public void setTargetLocation(@NotNull Location location) {
        this.targetLocation = location;
        this.velocity = this.location.clone().subtract(targetLocation).toVector()
            .multiply(new Vector(1, 0, 1))
            .normalize().multiply(0.5)
            .add(new Vector(0, 8, 0));
    }
}
