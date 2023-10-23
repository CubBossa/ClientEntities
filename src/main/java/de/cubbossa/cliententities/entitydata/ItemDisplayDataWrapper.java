package de.cubbossa.cliententities.entitydata;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.inventory.ItemStack;

public class ItemDisplayDataWrapper extends DisplayDataWrapper {

  protected ItemDisplayDataWrapper() {}

  public static class Item extends AbstractEntityDataWrapper {
    public Item(ItemStack stack) {
      this(SpigotConversionUtil.fromBukkitItemStack(stack));
    }

    public Item(com.github.retrooper.packetevents.protocol.item.ItemStack stack) {
      super(EntityDataTypes.ITEMSTACK, stack);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
        return -1;
      }
      if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
        return 23;
      }
      return 22;
    }
  }

  public static class DisplayType extends AbstractEntityDataWrapper {
    public DisplayType(ItemDisplayType displayType) {
      this((byte) displayType.ordinal());
    }

    public DisplayType(byte index) {
      super(EntityDataTypes.BYTE, index);
    }

    @Override
    protected int versionedIndex() {
      if (serverVersion.isOlderThan(ServerVersion.V_1_19_4)) {
        return -1;
      }
      if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
        return 24;
      }
      return 23;
    }
  }

  public enum ItemDisplayType {
    NONE,
    THIRD_PERSON_LEFT_HAND,
    THIRD_PERSON_RIGHT_HAND,
    FIRST_PERSON_LEFT_HAND,
    FIRST_PERSON_RIGHT_HAND,
    HEAD,
    GUI,
    GROUND,
    FIXED
  }
}
