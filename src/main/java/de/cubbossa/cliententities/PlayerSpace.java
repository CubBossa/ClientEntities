package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.cubbossa.cliententities.entity.*;
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

public class PlayerSpace implements Closeable {

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

  public static PlayerSpace createTicked() {
    return createTicked(50);
  }

  public static PlayerSpace createTicked(int ms) {
    Timer timer = new Timer();
    PlayerSpace playerSpace = new PlayerSpace(Collections.emptyList()) {
      @Override
      public void close() throws IOException {
        super.close();
        timer.cancel();
      }
    };
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        playerSpace.announceEntityRemovals();
        playerSpace.announce();
      }
    }, ms, ms);
    return playerSpace;
  }

  public static PlayerSpace create() {
    return new PlayerSpace(Collections.emptyList());
  }

  public static PlayerSpace create(Player... players) {
    return new PlayerSpace(Arrays.stream(players).map(Player::getUniqueId).toList());
  }

  public static PlayerSpace create(UUID... players) {
    return new PlayerSpace(Arrays.stream(players).toList());
  }

  private static int entityIdCounter = (int) 1e5;
  private static final TreeSet<Integer> freeEntityIds = new TreeSet<>();

  ClientEntityListener clientEntityListener;

  Collection<UUID> players;
  Map<Integer, ClientEntity> entities;
  Map<Class<? extends Event>, Map<UUID, Consumer<Event>>> listeners;

  private PlayerSpace(Collection<UUID> players) {
    this.players = new HashSet<>(players);
    this.entities = new HashMap<>();
    listeners = new HashMap<>();

    clientEntityListener = new ClientEntityListener(Executors.newSingleThreadExecutor(), this);
    PacketEvents.getAPI().getEventManager().registerListener(clientEntityListener);
  }

  @Override
  public void close() throws IOException {
    PacketEvents.getAPI().getEventManager().unregisterListener(clientEntityListener);
  }

  /**
   * Reserves an ID for a client entity.
   * All client entities IDs are created by decrementing a start value (10^5).
   * If an entity becomes released via {@link #releaseEntity(ClientEntity)}, the ID will be added to a pool.
   * As long as the pool still contains IDs they will be preferred to the decrement.
   *
   * @return A new ID for a client entity.
   */
  int claimId() {
    Integer id = freeEntityIds.pollLast();
    if (id == null) {
      id = entityIdCounter--;
    }
    return id;
  }

  public void addPlayerIfAbsent(Player player) {
    if (!players.contains(player.getUniqueId())) {
      addPlayer(player);
    }
  }

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

  public void removePlayer(Player player) {
    if (players.remove(player.getUniqueId())) {
      int[] ids = entities.keySet().stream().mapToInt(Integer::intValue).toArray();
      PacketEvents.getAPI().getPlayerManager().sendPacket(player,
          new WrapperPlayServerDestroyEntities(ids)
      );
    }
  }

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
      freeEntityIds.add(id);
    }
  }

  public FallingBlock spawnFallingBlock(Location location, BlockData blockData) {
    FallingBlock fallingBlock = spawn(location, FallingBlock.class);
    ((ClientFallingBlock) fallingBlock).setBlockData(blockData);
    return fallingBlock;
  }

  public <E extends Entity, C extends ClientEntity> C spawn(Location location, Class<E> type) {
    Class<C> mapping = (Class<C>) classConversion.get(type);
    if (mapping == null) {
      throw new IllegalArgumentException("No client entity implementation for entity type " + type + ".");
    }
    try {
      Constructor<?> constructor = mapping.getConstructor(PlayerSpace.class, int.class);
      C entity = (C) constructor.newInstance(this, claimId());
      entities.put(entity.getEntityId(), entity);
      entity.teleport(location);
      return entity;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <EventT extends Event> ListenerHandle registerListener(Class<EventT> event, Consumer<EventT> handler) {
    ListenerHandle handle = new ListenerHandle(event, UUID.randomUUID());
    listeners.computeIfAbsent(event, c -> new HashMap<>()).put(handle.id(), (Consumer<Event>) handler);
    return handle;
  }

  public void unregisterListener(ListenerHandle handle) {
    listeners.computeIfAbsent(handle.type(), c -> new HashMap<>()).remove(handle.id());
  }

  public <EventT extends Event> void callEvent(EventT event) {
    listeners.forEach((key, value) -> {
      if (key.isInstance(event)) {
        value.values().forEach(eventConsumer -> eventConsumer.accept(event));
      }
    });
  }

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

  public void announce() {
    Collection<Player> c = getPlayers();
    entities.values().stream()
        .map(entity -> (UntickedEntity) entity)
        .forEach(entity -> entity.announce(c));
  }

  public void announce(Entity e) {
    if (!(e instanceof UntickedEntity untickedEntity)) {
      throw new IllegalArgumentException("Entity is no client side entity: " + e.getEntityId());
    }
    untickedEntity.announce(getPlayers());
  }

  public Collection<Player> getPlayers() {
    return players.stream()
        .map(Bukkit::getPlayer)
        .collect(Collectors.toList());
  }

  public record ListenerHandle(Class<? extends Event> type, UUID id) {

  }
}
