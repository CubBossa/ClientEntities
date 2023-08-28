package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpace;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class ClientItemDisplay extends ClientDisplay implements ItemDisplay {

    private ItemStack itemStack = null;
    private ItemDisplayTransform itemDisplayTransform = ItemDisplayTransform.NONE;

    public ClientItemDisplay(PlayerSpace playerSpace, int entityId, EntityType entityType) {
        super(playerSpace, entityId, entityType);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (itemStack != null) {
            data.add(new EntityData(22, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack)));
        }
        if (itemDisplayTransform != ItemDisplayTransform.NONE) {
            data.add(new EntityData(23, EntityDataTypes.BYTE, (byte) itemDisplayTransform.ordinal()));
        }
        return data;
    }

    @Override
    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = setMeta(this.itemStack, itemStack);
    }

    @Override
    public void setItemDisplayTransform(@NotNull ItemDisplay.ItemDisplayTransform itemDisplayTransform) {
        this.itemDisplayTransform = setMeta(this.itemDisplayTransform, itemDisplayTransform);
    }
}
