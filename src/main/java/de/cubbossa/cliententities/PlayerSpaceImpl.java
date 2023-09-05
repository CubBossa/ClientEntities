package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.cubbossa.cliententities.entity.*;
import de.cubbossa.dontshade.cliententities.EntityIdGenerator;
import de.cubbossa.dontshade.cliententities.EntityIdProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerSpaceImpl implements PlayerSpace {

  public static BiMap<Class<? extends Entity>, Class<? extends ClientEntity>> classConversion = HashBiMap.create();

  static {
    classConversion.put(ArmorStand.class, ClientArmorStand.class);
    classConversion.put(BlockDisplay.class, ClientBlockDisplay.class);
    classConversion.put(TextDisplay.class, ClientTextDisplay.class);
    classConversion.put(Interaction.class, ClientInteraction.class);
    classConversion.put(EnderPearl.class, ClientEnderPearl.class);
    classConversion.put(Firework.class, ClientFireWork.class);
    classConversion.put(EnderSignal.class, ClientEyeOfEnder.class);
    classConversion.put(ItemDisplay.class, ClientItemDisplay.class);
    classConversion.put(Egg.class, ClientEgg.class);
    classConversion.put(ThrownExpBottle.class, ClientThrownExpBottle.class);
    classConversion.put(ThrownPotion.class, ClientThrownPotion.class);
    classConversion.put(FallingBlock.class, ClientFallingBlock.class);
    classConversion.put(Snowball.class, ClientSnowball.class);
  }

  EntityIdGenerator entityIdGenerator = EntityIdProvider.get();
  @Nullable ClientEntityListener clientEntityListener;
  Collection<UUID> players;
  Map<Integer, ClientEntity> entities;
  Map<Class<? extends Event>, Map<UUID, Consumer<Event>>> listeners;

  PlayerSpaceImpl(Collection<UUID> players, boolean defaultListener) {
    this.players = new HashSet<>(players);
    this.entities = new HashMap<>();
    listeners = new HashMap<>();

    if (defaultListener) {
      clientEntityListener = new ClientEntityListener(Executors.newSingleThreadExecutor(), this);
      PacketEvents.getAPI().getEventManager().registerListener(clientEntityListener);
    }
  }

  @Override
  public void close() throws IOException {
    PacketEvents.getAPI().getEventManager().unregisterListener(clientEntityListener);
  }

  @Override
  public void addPlayerIfAbsent(Player player) {
    if (!players.contains(player.getUniqueId())) {
      addPlayer(player);
    }
  }

  @Override
  public void addPlayer(Player player) {
    players.add(player.getUniqueId());
    Collection<Player> singletonList = Collections.singletonList(player);
    entities.values().stream()
        .filter(e -> !e.isAliveChanged())
        .forEach(clientEntity -> {
          clientEntity.setAliveChanged(true);
          clientEntity.announce(singletonList);
          clientEntity.setAliveChanged(false);
        });
  }

  @Override
  public void removePlayer(Player player) {
    if (players.remove(player.getUniqueId())) {
      int[] ids = entities.keySet().stream().mapToInt(Integer::intValue).toArray();
      PacketEvents.getAPI().getPlayerManager().sendPacket(player,
          new WrapperPlayServerDestroyEntities(ids)
      );
    }
  }

  @Override
  public @Nullable ClientEntity getEntity(int id) {
    return entities.get(id);
  }

  /**
   * Removes the entity from the map and frees the id.
   * The id can then be used for a new entity later.
   */
  public void releaseEntity(ClientEntity entity) {
    releaseEntity(entity.getEntityId());
  }

  /**
   * Removes the entity from the map and frees the id.
   * The id can then be used for a new entity later.
   */
  void releaseEntity(int id) {
    if (entities.remove(id) != null) {
      entityIdGenerator.releaseEntityId(id);
    }
  }

  @Override
  public ClientPlayer spawnPlayer(Location location, UUID uuid, String name) {
    ClientPlayer player = new ClientPlayer(this, name) {
      @Override
      public UUID createId() {
        return uuid;
      }
    };
    entities.put(player.getEntityId(), player);
    player.teleport(location);
    return player;
  }

  @Override
  public ClientFallingBlock spawnFallingBlock(Location location, BlockData blockData) {
    ClientFallingBlock fallingBlock = spawn(location, FallingBlock.class);
    fallingBlock.setBlockData(blockData);
    return fallingBlock;
  }

  @Override
  public ClientExperienceOrb spawnExpOrb(Location location, short count) {
    ClientExperienceOrb orb = new ClientExperienceOrb(this, entityIdGenerator.nextEntityId(), location, count);
    entities.put(orb.getEntityId(), orb);
    return orb;
  }

  @Override
  public <E extends Entity, C extends ClientEntity> C spawn(Location location, Class<E> type) {
    Class<C> mapping = (Class<C>) classConversion.get(type);
    if (mapping == null) {
      throw new IllegalArgumentException("No client entity implementation for entity type " + type + ".");
    }
    try {
      Constructor<?> constructor = mapping.getConstructor(PlayerSpaceImpl.class, int.class);
      C entity = (C) constructor.newInstance(this, entityIdGenerator.nextEntityId());
      entities.put(entity.getEntityId(), entity);
      entity.teleport(location);
      return entity;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <EventT extends Event> ListenerHandle registerListener(Class<EventT> event, Consumer<EventT> handler) {
    ListenerHandle handle = new ListenerHandle(event, UUID.randomUUID());
    listeners.computeIfAbsent(event, c -> new HashMap<>()).put(handle.id(), (Consumer<Event>) handler);
    return handle;
  }

  @Override
  public void unregisterListener(ListenerHandle handle) {
    listeners.computeIfAbsent(handle.type(), c -> new HashMap<>()).remove(handle.id());
  }

  @Override
  public <EventT extends Event> void callEvent(EventT event) {
    listeners.forEach((key, value) -> {
      if (key.isInstance(event)) {
        value.values().forEach(eventConsumer -> eventConsumer.accept(event));
      }
    });
  }

  @Override
  public void announceEntityRemovals() {
    int[] ids = entities.values().stream()
        .filter(e -> e.isAliveChanged() && e.isDead())
        .peek(e -> e.setAliveChanged(false))
        .mapToInt(ClientEntity::getEntityId)
        .toArray();

    PacketEventsAPI<?> api = PacketEvents.getAPI();

    for (Player player : getPlayers()) {

      api.getPlayerManager().sendPacket(player,
          new WrapperPlayServerDestroyEntities(ids)
      );
    }
    for (int id : ids) {
      releaseEntity(id);
    }
  }

  @Override
  public void announce() {
    Collection<Player> c = getPlayers();
    entities.values().stream()
        .map(entity -> (UntickedEntity) entity)
        .forEach(entity -> entity.announce(c));
  }

  @Override
  public void announce(Entity e) {
    if (!(e instanceof UntickedEntity untickedEntity)) {
      throw new IllegalArgumentException("Entity is no client side entity: " + e.getEntityId());
    }
    untickedEntity.announce(getPlayers());
  }

  @Override
  public Collection<Player> getPlayers() {
    return players.stream()
        .map(Bukkit::getPlayer)
        .collect(Collectors.toList());
  }

}
