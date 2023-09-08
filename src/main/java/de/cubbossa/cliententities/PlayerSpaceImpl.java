package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    classConversion.put(Villager.class, ClientVillager.class);
    classConversion.put(LeashHitch.class, ClientLeashHitch.class);
  }

  EntityIdGenerator entityIdGenerator = EntityIdProvider.get();
  @Nullable ClientEntityListener clientEntityListener;
  Collection<UUID> players;
  Map<Integer, ClientEntity> entities;
  Map<Class<? extends Event>, Map<UUID, Consumer<Event>>> listeners;

  PlayerSpaceImpl(Collection<UUID> players, boolean defaultListener) {
    this.players = ConcurrentHashMap.newKeySet();
    this.players.addAll(players);
    this.entities = new ConcurrentHashMap<>();
    this.listeners = new ConcurrentHashMap<>();

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
        .filter(e -> !e.isDead())
        .forEach(clientEntity -> {
          handleUpdateInfoList(clientEntity.spawn(false), singletonList);
          handleUpdateInfoList(clientEntity.state(false), singletonList);
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
  public void announce() {
    Collection<Player> players = getPlayers();
    entities.values().stream()
        .map(entity -> (ClientViewElement) entity)
        .peek(entity -> handleUpdateInfoList(entity.spawn(true), players))
        .peek(entity -> handleUpdateInfoList(entity.state(true), players))
        .forEach(entity -> handleUpdateInfoList(entity.delete(true), players));
  }

  private void handleUpdateInfoList(List<ClientViewElement.UpdateInfo> list, Collection<Player> players) {
    List<PacketWrapper<?>> wrappers = list.stream()
        .filter(updateInfo -> updateInfo instanceof ClientViewElement.PacketInfo)
        .map(updateInfo -> (ClientViewElement.PacketInfo) updateInfo)
        .map(ClientViewElement.PacketInfo::wrapper)
        .collect(Collectors.toList());

    List<ClientViewElement.CombineInfo> combines = list.stream()
        .filter(updateInfo -> updateInfo instanceof ClientViewElement.CombineInfo)
        .map(updateInfo -> (ClientViewElement.CombineInfo) updateInfo)
        .collect(Collectors.toList());
    if (!combines.isEmpty()) {
      ClientViewElement.CombineInfo first = combines.remove(0);
      while (!combines.isEmpty()) {
        ClientViewElement.CombineInfo info = combines.remove(0);
        first = first.merge(info);
      }
      wrappers.add(first.wrapper());
    }
    var pm = PacketEvents.getAPI().getPlayerManager();
    for(Player player : players) {
      wrappers.forEach(p -> pm.sendPacket(p, player));
    }
  }

  @Override
  public Collection<Player> getPlayers() {
    return players.stream()
        .map(Bukkit::getPlayer)
        .collect(Collectors.toList());
  }
}
