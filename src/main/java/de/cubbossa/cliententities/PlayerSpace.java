package de.cubbossa.cliententities;

import de.cubbossa.cliententities.entity.ClientEntity;
import de.cubbossa.cliententities.entity.ClientExperienceOrb;
import de.cubbossa.cliententities.entity.ClientFallingBlock;
import de.cubbossa.cliententities.entity.ClientPlayer;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public interface PlayerSpace extends Closeable {

  static Builder builder() {
    return new Builder();
  }

  // Members

  Collection<Player> getPlayers();

  void addPlayerIfAbsent(Player player);

  void addPlayer(Player player);

  void removePlayer(Player player);


  // Entities

  @Nullable
  ClientEntity getEntity(int id);

  ClientPlayer spawnPlayer(Location location, UUID uuid, String name);

  ClientExperienceOrb spawnExpOrb(Location location, short count);

  ClientFallingBlock spawnFallingBlock(Location location, BlockData blockData);

  <E extends Entity, C extends ClientEntity> C spawn(Location location, Class<E> type);


  // Events

  <EventT extends Event> ListenerHandle registerListener(Class<EventT> event, Consumer<EventT> handler);

  void unregisterListener(ListenerHandle handle);

  <EventT extends Event> void callEvent(EventT event);


  // Ticks

  void announceEntityRemovals();

  void announce();

  void announce(Entity e);



  class Builder {
    long period = -1;
    Collection<UUID> ids = new HashSet<>();
    boolean events = false;

    private Builder() {
    }

    public Builder withInterval(int ms) {
      this.period = ms;
      return this;
    }

    public Builder withTickInterval() {
      this.period = 50;
      return this;
    }

    public Builder withPlayer(Player player) {
      return withPlayer(player.getUniqueId());
    }

    public Builder withPlayer(UUID uuid) {
      this.ids.add(uuid);
      return this;
    }

    public Builder withPlayers(Iterable<Player> players) {
      players.forEach(player -> ids.add(player.getUniqueId()));
      return this;
    }

    public Builder withPlayersByID(Iterable<UUID> players) {
      players.forEach(ids::add);
      return this;
    }

    public Builder withEventSupport() {
      events = true;
      return this;
    }

    public PlayerSpaceImpl build() {
      Timer timer = new Timer();
      PlayerSpaceImpl playerSpace = new PlayerSpaceImpl(ids, events) {
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
      }, period, period);
      return playerSpace;
    }
  }

  public record ListenerHandle(Class<? extends Event> type, UUID id) {

  }
}
