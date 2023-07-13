package com.teenkung.enderenigma.manager;

import com.teenkung.enderenigma.EnderEnigma;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private EnderEnigma plugin;
    private Map<Player, PlayerDataManager> map = new HashMap<>();

    public PlayerManager(EnderEnigma plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        map.put(player, new PlayerDataManager(player, plugin));
    }

    public PlayerDataManager getPlayer(Player player) {
        return map.get(player);
    }

    public void removePlayer(Player player) {
        map.remove(player);
    }
}
