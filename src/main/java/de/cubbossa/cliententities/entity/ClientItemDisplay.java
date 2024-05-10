package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
import de.cubbossa.cliententities.entitydata.ItemDisplayDataWrapper;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientItemDisplay extends ClientDisplay implements ItemDisplay {

    TrackedField<ItemStack> itemStack = new TrackedField<>(new ItemStack(Material.AIR));
    TrackedField<ItemDisplay.ItemDisplayTransform> itemDisplayTransform = new TrackedField<>(ItemDisplay.ItemDisplayTransform.NONE);

    public ClientItemDisplay(PlayerSpaceImpl playerSpace, int entityId, EntityType entityType) {
        super(playerSpace, entityId, entityType);
    }

    @Override
    List<EntityData> metaData() {
        List<EntityData> data = super.metaData();
        if (itemStack.hasChanged()) {
            data.add(ItemDisplayDataWrapper.item(SpigotConversionUtil.fromBukkitItemStack(itemStack.getValue())));
        }
        if (itemDisplayTransform.hasChanged()) {
            data.add(ItemDisplayDataWrapper.displayType((byte) itemDisplayTransform.getValue().ordinal()));
        }
        return data;
    }

    @Nullable
    @Override
    public ItemStack getItemStack() {
        return itemStack.getValue();
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
    setMeta(this.itemStack, itemStack);
    }

    @NotNull
    @Override
    public ItemDisplayTransform getItemDisplayTransform() {
        return itemDisplayTransform.getValue();
    }

    public void setItemDisplayTransform(@NotNull ItemDisplay.ItemDisplayTransform itemDisplayTransform) {
    setMeta(this.itemDisplayTransform, itemDisplayTransform);
    }
}
