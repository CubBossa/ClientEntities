package de.cubbossa.cliententities.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientEntityTest {

  @BeforeAll
  static void init() {
  }

  @Test
  void spawn() {
  }

  @Test
  void announce() {
  }

  @Test
  void data() {
  }

  @Test
  void metaData() {
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(null));
    ClientEntity entity = new ClientEntity(null, 1, EntityType.BAT);
    assertEquals(0, entity.metaData().size());
    entity.setGlowing(true);
    assertEquals(1, entity.metaData().size());
  }
}