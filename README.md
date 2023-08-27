# ClientEntities

A wrapper for the PacketEvents library to easily create client side entities.
It uses the Spigot API entity classes, so that the general usage is familiar.

## How to use

Main part of the library is the PlayerSpace class. It resembles a group of players that are supposed to see some client
side entity action.
If you want two players to see a client item stack hovering above a chest shop, create a PlayerSpace instance for these
two players with
```Java
PlayerSpace playerSpace = PlayerSpace.create(playerA, playerB);
```

You should keep the reference to later remove the client entities. Also, whenever you used your PlayerSpace and don't need
it anymore, call `PlayerSpace#close()` to unregister its listeners.

### Summoning

Entities can be spawned similar to the Spigot API. Some entity methods will throw an exception `ClientEntityMethodNotSupportedException`,
which basically means that the method would not have any influence on the client entity and should therefore not be used. These
are server-side methods.

```Java 
DisplayText x = playerSpace.spawn(myLocation, DisplayText.class)
```

**Valid entities for now are:**
- TextDisplay
- BlockDisplay
- Interaction
- Snowball
- Item
- Egg
- ArmorStand
- FallingBlock

More will come. If you require one entity in particular, just make an issue and I will add it asap.

### Listeners 

There are wrappings for the entity interaction events.
You can therefore use
```Java
ListenerHandler listener = playerSpace.registerListener(PlayerInteractEntityEvent.class, e -> {
    // do something with the event like with Spigot API
});
playerSpace.unregisterListener(listener);
```

**Valid events for now are:**
- PlayerInteractEntityEvent
- PlayerInteractAtEntityEvent
- EntityDamageByEntityEvent


### Comparison
#### ClientEntities

````Java
import de.cubbossa.cliententities.PlayerSpace;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.UUID;

class Fountain {
  PlayerSpace playerSpace;
  Location location;
  Vector direction;

  public Fountain(UUID... players) {
    playerSpace = PlayerSpace.create(players);
  }

  void play() {
    FallingBlock fountain = playerSpace.spawnFallingBlock(location, Material.WATER.createBlockData());
    fountain.setVelocity(direction);
    playerSpace.announce(fountain);
  }
}
````

**Advantages**
- Custom update interval, not tick bound
- Close to identical syntax to Spigot API, no learning of new API
- Client-side only, only chosen players see the entity
- No game state changes - the entity cannot change anything about the world
- No server side lag

**Disadvantages**
- Every effect of the entity must be implemented by yourself (A falling block will e.g. not turn into a solid block when landing)
- Bound to PacketEntities library

#### Spigot

````Java
import de.cubbossa.cliententities.PlayerSpace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

class Fountain {
  final Plugin plugin;
  final Collection<UUID> players;
  Location location;
  Vector direction;

  public Fountain(Plugin plugin, UUID... players) {
    this.plugin = plugin;
    this.players = Arrays.asList(players);
  }

  void play() {
    FallingBlock fountain = location.getWorld().spawnFallingBlock(location, Material.WATER.createBlockData());
    fountain.setPersistent(false);
    fountain.setVisibleByDefault(false);
    fountain.setVelocity(direction);
    playerSpace.announce(fountain);
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (!players.contains(online.getUniqueId())) {
        continue;
      }
      online.showEntity(fountain, plugin);
    }
  }
}
````

**Advantages**
- No additional libraries required

**Disadvantages**
- Errors in code might change the actual world
- Bound to main thread
- Tons of entities can influence the TPS drastically


## How to build
