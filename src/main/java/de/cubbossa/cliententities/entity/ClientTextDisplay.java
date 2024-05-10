package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import de.cubbossa.cliententities.PlayerSpaceImpl;
import de.cubbossa.cliententities.TrackedBoolField;
import de.cubbossa.cliententities.TrackedField;
import de.cubbossa.cliententities.entitydata.TextDisplayDataWrapper;
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
    super(playerSpace, entityId, EntityTypes.TEXT_DISPLAY);
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
      data.add(TextDisplayDataWrapper.text(text.getValue()));
      text.flushChanged();
    }
    if (lineWidth.hasChanged()) {
      data.add(TextDisplayDataWrapper.textWidth(lineWidth.getValue()));
      lineWidth.flushChanged();
    }
    if (backgroundColor.hasChanged()) {
      data.add(TextDisplayDataWrapper.backgroundColor(backgroundColor.getValue() == null ? 0x40000000 : backgroundColor.getValue().asARGB()));
      backgroundColor.flushChanged();
    }
    if (textOpacity.hasChanged()) {
      data.add(TextDisplayDataWrapper.textOpacity(textOpacity.getValue()));
      textOpacity.flushChanged();
    }
    if (shadowed.hasChanged() || seeThrough.hasChanged() || defaultBackground.hasChanged() || alignment.hasChanged()) {
      data.add(TextDisplayDataWrapper.options(
          shadowed.getValue(),
          seeThrough.getBooleanValue(),
          defaultBackground.getBooleanValue(),
          TextDisplayDataWrapper.TextAlignment.values()[alignment.getValue().ordinal()]
      ));
      shadowed.flushChanged();
      seeThrough.flushChanged();
      defaultBackground.flushChanged();
      alignment.flushChanged();
    }
    return data;
  }
}
