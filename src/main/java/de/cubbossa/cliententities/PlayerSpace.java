package de.cubbossa.cliententities;

import de.cubbossa.cliententities.entity.ClientEntity;
import de.cubbossa.cliententities.entity.ClientExperienceOrb;
import de.cubbossa.cliententities.entity.ClientFallingBlock;
import de.cubbossa.cliententities.entity.ClientGuardianBeam;
import de.cubbossa.cliententities.entity.ClientPlayer;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public interface PlayerSpace extends Closeable {

  /**
   * Creates a new PlayerSpace builder.
   * <br>
   * Use {@link #close()} to remove this PlayerSpace without remaining client entities or listeners.
   *
   * @param players The players to initially add to the PlayerSpace. Players can be added later.
   * @return a builder that will construct the PlayerSpace.
   */
  static Builder create(Collection<UUID> players) {
    return new Builder().withPlayersByID(players);
  }

  /**
   * Creates a new PlayerSpace builder.
   * <br>
   * Use {@link #close()} to remove this PlayerSpace without remaining client entities or listeners.
   *
   * @return a builder that will construct the PlayerSpace.
   */
  static Builder create() {
    return new Builder();
  }

  /**
   * Creates a new PlayerSpace with all online players. Whenever a player joins or quits, the PlayerSpace will be updated.
   * <br>
   * Use {@link #close()} to remove this PlayerSpace without remaining client entities or listeners.
   *
   * @param plugin Your plugin to register the according join and quit event on.
   * @return a builder that will construct a global PlayerSpace.
   */
  static Builder createGlobal(Plugin plugin) {
    return new Builder() {
      public PlayerSpaceImpl build() {
        if (period >= 1) {
          Timer timer = new Timer();
          PlayerSpaceImpl playerSpace = new GlobalPlayerSpace(plugin, events) {
            @Override
            public void close() throws IOException {
              super.close();
              timer.cancel();
            }
          };
          timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
              playerSpace.announce();
            }
          }, period, period);
          return playerSpace;
        }
        return new GlobalPlayerSpace(plugin, events);
      }
    };
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

  ClientGuardianBeam spawnGuardianBeam(Location location, Location target);

  ClientGuardianBeam spawnGuardianBeam(Location location, Entity target);

  <E extends Entity, C extends ClientEntity> C spawn(Location location, Class<E> type);

  <C extends ClientEntity> C spawnClient(Location location, Class<C> type);

  // Events

  <EventT extends Event> Listener<EventT> registerListener(Class<EventT> event, Consumer<EventT> handler);

  <EventT extends Event> void unregisterListener(Listener<EventT> handle);

  <EventT extends Event> void callEvent(EventT event);


  /**
   * An order of packet processing is required to maintain valid states.
   * Imagine following events:
   * - Spawn A
   * - Spawn B
   * - Set B as passenger of A
   * - announce();
   * If simply announcing all entities in order, A would first be spawned, then its meta would be updated.
   * Only after that, B would be spawned, even though it was already used in the update call of A.
   * <br>
   * To deal with this order, we simply spawn all entities at the start of one tick, update all entities after that
   * and in the last step delete all entities that are supposed to be deleted.
   * If certain more updates are needed, more PlayerSpaces or more announce() calls are necessary.
   */
  void announce();

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
      if (period >= 1) {
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
            playerSpace.announce();
          }
        }, period, period);
        return playerSpace;
      }
      return new PlayerSpaceImpl(ids, events);
    }
  }

  interface Listener<E extends Event> {

    Class<E> getType();

    void accept(E event);
  }
}
