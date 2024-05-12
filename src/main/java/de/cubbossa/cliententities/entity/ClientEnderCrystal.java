package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.TrackedField;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.jetbrains.annotations.Nullable;

public class ClientEnderCrystal extends ClientEntity implements EnderCrystal {

  TrackedBoolField showingBottom = new TrackedBoolField(true);
  TrackedField<@Nullable Location> beamTarget = new TrackedField<>();

  public ClientEnderCrystal(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityTypes.END_CRYSTAL);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (showingBottom.hasChanged()) {
      data.add(new EntityData(8, EntityDataTypes.BOOLEAN, showingBottom.getBooleanValue()));
    }
    data.add(new EntityData(9, EntityDataTypes.OPTIONAL_BLOCK_POSITION, beamTarget.getValue() == null
        ? Optional.empty()
        : Optional.of(SpigotConversionUtil.fromBukkitLocation(beamTarget.getValue()))));
    return data;
  }

  @Override
  public boolean isShowingBottom() {
    return showingBottom.getBooleanValue();
  }

  @Override
  public void setShowingBottom(boolean b) {
    setMeta(this.showingBottom, b);
  }

  @Nullable
  @Override
  public Location getBeamTarget() {
    return beamTarget.getValue();
  }

  @Override
  public void setBeamTarget(@Nullable Location location) {
    setMeta(this.beamTarget, location);
  }
}
