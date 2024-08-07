package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import de.cubbossa.cliententities.entity.ClientEntity;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class ClientEntityListener extends PacketListenerAbstract {

  private final Executor executor;
  private final PlayerSpaceImpl playerSpace;

  public ClientEntityListener(Executor executor, PlayerSpaceImpl playerSpace) {
    super(PacketListenerPriority.HIGH);
    this.executor = executor;
    this.playerSpace = playerSpace;
  }

  @Override
  public void onPacketReceive(PacketReceiveEvent event) {
    super.onPacketReceive(event);
    if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
      callWithExecutor(event, e -> {
        WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(e);
        ClientEntity target = playerSpace.getEntity(wrapper.getEntityId());
        if (target == null) {
          return;
        }
        switch (wrapper.getAction()) {
          case INTERACT_AT -> {
            Vector at = wrapper.getTarget().map(v -> new Vector(v.x, v.y, v.z)).orElse(null);
            playerSpace.callEvent(new PlayerInteractAtEntityEvent(Bukkit.getPlayer(e.getUser().getUUID()),
            target, at));
          }
          case INTERACT -> playerSpace.callEvent(new PlayerInteractEntityEvent(Bukkit.getPlayer(e.getUser().getUUID()),
          target));
          case ATTACK -> {
            try {
                playerSpace.callEvent(new EntityDamageByEntityEvent(
                Bukkit.getPlayer(e.getUser().getUUID()),
                target, EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                DamageSource.builder(DamageType.MOB_ATTACK).build(), 0
                ));
            } catch (NoClassDefFoundError ex) {
              playerSpace.callEvent(new EntityDamageByEntityEvent(
              Bukkit.getPlayer(e.getUser().getUUID()),
              target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0
              ));
            }
          }
        }
      });
    }
  }

  private void callWithExecutor(PacketReceiveEvent event, Consumer<PacketReceiveEvent> consumer) {
    PacketReceiveEvent copy = event.clone();
    executor.execute(() -> {
      consumer.accept(copy);
      copy.cleanUp();
    });
  }
}
