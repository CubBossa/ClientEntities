package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.TrackedField;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientTextDisplay extends ClientDisplay implements TextDisplay {

  TrackedField<@Nullable Component> text = new TrackedField<>();
  TrackedField<Integer> lineWidth = new TrackedField<>(200);
  TrackedField<@Nullable Color> backgroundColor = new TrackedField<>(Color.fromARGB(0x40, 0, 0, 0));
  TrackedField<Byte> textOpacity = new TrackedField<>((byte) -1);
  TrackedBoolField shadowed = new TrackedBoolField();
  TrackedBoolField seeThrough = new TrackedBoolField();
  TrackedBoolField defaultBackground = new TrackedBoolField();
  TrackedField<TextDisplay.TextAlignment> alignment = new TrackedField<>(TextDisplay.TextAlignment.CENTER);

  public ClientTextDisplay(PlayerSpaceImpl playerSpace, int entityId) {
    super(playerSpace, entityId, EntityType.TEXT_DISPLAY);
  }

  public String getText() {
    return text.getValue() == null ? "" : GSON.serialize(text.getValue());
  }

  public void setText(@Nullable String text) {
    setMeta(this.text, text == null ? null : Component.text(text));
  }

  @Override
  public int getLineWidth() {
    return lineWidth.getValue();
  }

  public void setLineWidth(int width) {
    setMeta(this.lineWidth, width);
  }

  @Nullable
  @Override
  public Color getBackgroundColor() {
    return backgroundColor.getValue();
  }

  public void setBackgroundColor(@Nullable Color color) {
    setMeta(this.backgroundColor, color);
  }

  @Override
  public byte getTextOpacity() {
    return textOpacity.getValue();
  }

  public void setTextOpacity(byte opacity) {
    setMeta(this.textOpacity, opacity);
  }

  @Override
  public boolean isShadowed() {
    return shadowed.getBooleanValue();
  }

  public void setShadowed(boolean shadow) {
    setMeta(this.shadowed, shadow);
  }

  @Override
  public boolean isSeeThrough() {
    return seeThrough.getBooleanValue();
  }

  public void setSeeThrough(boolean seeThrough) {
    setMeta(this.seeThrough, seeThrough);
  }

  @Override
  public boolean isDefaultBackground() {
    return defaultBackground.getBooleanValue();
  }

  public void setDefaultBackground(boolean defaultBackground) {
    setMeta(this.defaultBackground, defaultBackground);
  }

  @NotNull
  @Override
  public TextAlignment getAlignment() {
    return alignment.getValue();
  }

  public void setAlignment(@NotNull TextDisplay.TextAlignment alignment) {
    setMeta(this.alignment, alignment);
  }

  @Override
  List<EntityData> metaData() {
    List<EntityData> data = super.metaData();
    if (text.hasChanged()) {
      data.add(new EntityData(22, EntityDataTypes.COMPONENT, text.getValue() == null
          ? Component.empty()
          : GSON.serialize(text.getValue())));
    }
    if (lineWidth.hasChanged()) {
      data.add(new EntityData(23, EntityDataTypes.INT, lineWidth.getValue()));
    }
    if (backgroundColor.hasChanged()) {
      data.add(new EntityData(24, EntityDataTypes.INT, backgroundColor.getValue() == null
          ? 0x40000000
          : backgroundColor.getValue().asARGB()));
    }
    if (textOpacity.hasChanged()) {
      data.add(new EntityData(25, EntityDataTypes.BYTE, textOpacity.getValue()));
    }
    if (shadowed.hasChanged() || seeThrough.hasChanged() || defaultBackground.hasChanged() || alignment.hasChanged()) {
      byte mask = (byte) (
          (shadowed.getBooleanValue() ? 0x01 : 0)
              | (seeThrough.getBooleanValue() ? 0x02 : 0)
              | (defaultBackground.getBooleanValue() ? 0x04 : 0)
              | (switch (alignment.getValue()) {
            case CENTER -> 0x00;
            case LEFT -> 0x08;
            case RIGHT -> 0x10;
          }));
      data.add(new EntityData(26, EntityDataTypes.BYTE, mask));
    }
    return data;
  }
}
