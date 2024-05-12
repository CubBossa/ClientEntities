package de.cubbossa.cliententities;

import java.io.IOException;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class GlobalPlayerSpace extends PlayerSpaceImpl implements PlayerSpace, Listener {

  GlobalPlayerSpace(Plugin plugin, boolean defaultListener) {
    super(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()), defaultListener);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void close() throws IOException {
    super.close();
    PlayerJoinEvent.getHandlerList().unregister(this);
    PlayerQuitEvent.getHandlerList().unregister(this);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    addPlayer(e.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    removePlayer(e.getPlayer());
  }
}
