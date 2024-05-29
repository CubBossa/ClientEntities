package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedField;
import de.cubbossa.cliententities.entitydata.SlimeDataWrapper;
import java.util.List;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

public class ClientSlime extends ClientMob implements Slime {

  TrackedField<Integer> size = new TrackedField<>(1);

  public ClientSlime(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.SLIME);
  }

  @Override
  List<EntityData> metaData() {
    var meta = super.metaData();
    if (size.hasChanged()) {
      meta.add(SlimeDataWrapper.size(size.getValue()));
    }
    return meta;
  }

  @Override
  public int getSize() {
    return size.getValue();
  }

  @Override
  public void setSize(int i) {
    setMeta(size, i);
  }

  @NotNull
  @Override
  public EntityCategory getCategory() {
    return EntityCategory.NONE;
  }
}
