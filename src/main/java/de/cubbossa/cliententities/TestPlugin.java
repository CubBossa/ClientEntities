package de.cubbossa.cliententities;

import com.github.retrooper.packetevents.PacketEvents;
import de.cubbossa.cliententities.entity.*;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        EntityType type = EntityType.valueOf(args[0].toUpperCase());
        switch (type) {
          case ENDER_SIGNAL -> {
            ClientEyeOfEnder enderSignal = playerSpace.spawn(player.getLocation(), EnderSignal.class);
            enderSignal.setVelocity(new Vector(0.5, 0.5, 0.5).multiply(1_000));
            playerSpace.announce();
          }
          case BLOCK_DISPLAY -> {
            IntStream.range(0, 16 * 16 * 16)
                .parallel()
                .mapToObj(i -> player.getLocation().clone().add(i % 16, (i % 256) / 16, i / 256))
                .map(l -> playerSpace.<BlockDisplay, ClientBlockDisplay>spawn(l, BlockDisplay.class))
                .peek(o -> o.setBlock(Material.AMETHYST_BLOCK.createBlockData()))
                .forEach(o -> {
                  Transformation t = o.getTransformation();
                  t.getScale().mul(0.05f);
                  o.setTransformation(t);
                });
            playerSpace.announce();
          }
          case AXOLOTL -> {
            Supplier<Location> targetLocation = () -> player.getLocation().add(player.getLocation().getDirection().normalize().multiply(10));
            ClientGuardianBeam beam = playerSpace.spawnGuardianBeam(player.getLocation(), targetLocation.get());
            playerSpace.announce();
            Timer timer = new Timer();
            AtomicInteger counter = new AtomicInteger(0);
            timer.scheduleAtFixedRate(new TimerTask() {
              @Override
              public void run() {
                if (counter.getAndIncrement() > 300) {
                  beam.remove();
                  playerSpace.announce();
                  timer.cancel();
                  return;
                }
                beam.setTargetLocation(targetLocation.get());
                playerSpace.announce();
              }
            }, 50, 50);
          }
          case SQUID -> {
            ClientSquid a = playerSpace.spawn(player.getLocation(), Squid.class);
            ClientSquid b = playerSpace.spawn(player.getLocation().add(1, 0, 0), Squid.class);
            ClientSquid c = playerSpace.spawn(player.getLocation().add(2, 0, 0), Squid.class);
            a.addPassenger(b);
            b.addPassenger(c);
            playerSpace.announce();
          }
          case FIREWORK -> {
            ClientFireWork firework = playerSpace.spawn(player.getLocation(), Firework.class);
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
          case VILLAGER -> {
            ClientVillager villager = playerSpace.spawn(player.getLocation(), Villager.class);
            villager.setProfession(Villager.Profession.LIBRARIAN);
            villager.setVillagerType(Villager.Type.SWAMP);
            villager.setVillagerLevel(3);
            villager.teleport(villager.getLocation().setDirection(new Vector(1, 1, 1)));
            villager.shakeHead();
            villager.setHeight(2);

            ClientVillager top = playerSpace.spawn(player.getLocation(), Villager.class);
            top.setBaby();
            villager.setPassenger(top);

            new Timer().scheduleAtFixedRate(new TimerTask() {
              @Override
              public void run() {
                villager.shakeHead();
                top.happy();
                playerSpace.announce();
              }
            }, 2000, 2000);
            playerSpace.announce();
          }
          case FALLING_BLOCK -> {

            Timer timer = new Timer();
            AtomicInteger i = new AtomicInteger();
            Vector vector = new Vector(1, 2, 0);
            timer.scheduleAtFixedRate(new TimerTask() {
              @Override
              public void run() {
                try {
                  if (i.getAndIncrement() > 16) {
                    timer.cancel();
                    i.set(0);
                  }
                  ClientFallingBlock block = playerSpace.spawnFallingBlock(player.getLocation().clone().add(player.getLocation().getDirection().normalize().multiply(3)), Material.COBBLESTONE.createBlockData());
                  block.setVelocity(vector);
                  vector.rotateAroundY(45 * Math.PI / 180);
                  playerSpace.announce();
                } catch (Throwable t) {
                  t.printStackTrace();
                }
              }
            }, 0, 200);
          }
          case LEASH_HITCH -> {
            playerSpace.spawn(player.getLocation(), LeashHitch.class);
            playerSpace.announce();
          }
          case PLAYER -> {
            ClientPlayer sim = playerSpace.spawnPlayer(player.getLocation(), UUID.randomUUID(), "Just1Moro");
            sim.setDisplayInTab(false);
            sim.setDisplayName(Component.text("Just1Moro"));
            sim.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
            sim.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
            sim.setSneaking(true);
            playerSpace.announce();
          }
          default -> {
            player.sendMessage("Not implemented");
          }
        }
      }
      return false;
    });
    getCommand("cliententities").setTabCompleter((commandSender, command, s, strings) -> {
      return Arrays.stream(EntityType.values())
          .map(t -> t.toString().toLowerCase())
          .filter(t -> t.startsWith(strings[0].toLowerCase()))
          .collect(Collectors.toList());
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
