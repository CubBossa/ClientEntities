package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TestPlugin extends JavaPlugin implements Listener {

  PlayerSpace playerSpace;

  @Override
  public void onLoad() {
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    PacketEvents.getAPI().load();
  }

  @Override
  public void onEnable() {
    PacketEvents.getAPI().init();
    playerSpace = PlayerSpace.builder().build();
    Bukkit.getPluginManager().registerEvents(this, this);

    getCommand("cliententities").setExecutor((sender, cmd, s, args) -> {
      Player player = (Player) sender;
      if (args.length > 0) {
        switch (args[0]) {

          case "eye_of_ender" -> {
            EnderSignal enderSignal = playerSpace.spawn(player.getLocation(), EnderSignal.class);
            enderSignal.setVelocity(new Vector(0.5, 0.5, 0.5).multiply(1_000));
            playerSpace.announce(enderSignal);
          }
          case "rocket" -> {
            Firework firework = playerSpace.spawn(player.getLocation(), Firework.class);
            ItemStack stack = new ItemStack(Material.FIREWORK_ROCKET);
            FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
            meta.setPower(1);
            meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(Color.AQUA)
                .withFlicker()
                .withTrail()
                .build());
            firework.setFireworkMeta(meta);
            firework.setVelocity(new Vector(0.5, 0.5, 0.5).multiply(1_000));
            firework.setShotAtAngle(true);
            firework.detonate();
            playerSpace.announce();
          }
        }
      }
      return false;
    });
  }

  @Override
  public void onDisable() {
    PacketEvents.getAPI().terminate();
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    playerSpace.addPlayer(event.getPlayer());
  }
}
