package com.teenkung.enderenigma.listeners;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitEvent implements Listener {

    private final EnderEnigma plugin;

    public JoinQuitEvent(EnderEnigma plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerDataManager manager = plugin.getPlayerManager().getPlayer(event.getPlayer());
        manager.saveData();
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getPlayerManager().removePlayer(event.getPlayer()), 20);
    }

}
