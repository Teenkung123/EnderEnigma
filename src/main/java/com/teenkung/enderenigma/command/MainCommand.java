package com.teenkung.enderenigma.command;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.config.BannerConfig;
import com.teenkung.enderenigma.config.ConfigLoader;
import com.teenkung.enderenigma.config.SubPoolConfig;
import com.teenkung.enderenigma.manager.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements TabExecutor {
    private EnderEnigma plugin;

    public MainCommand(EnderEnigma plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "open":
                    if (sender.hasPermission("enderenigma.open") && sender instanceof Player player) {
                        if (args.length == 3) {
                            ConfigLoader config = plugin.getConfigLoader();
                            if (config.getBannerList().contains(args[1])) {
                                PlayerDataManager manager = plugin.getPlayerManager().getPlayer(player);
                                ArrayList<SubPoolConfig> pools = manager.requestPull(args[1], Integer.parseInt(args[2]));

                            }
                        }
                    } else {
                        //Send no permission message
                    }
                    break;
                case "force-open":
                    if (sender.hasPermission("enderenigma.force-open")) {
                        // logic for force-open
                    } else {
                        //Send no permission message
                    }
                    break;
                case "logs":
                    if (sender.hasPermission("enderenigma.logs")) {
                        // logic for logs
                    } else {
                        //Send no permission message
                    }
                    break;
                case "info":
                    if (sender.hasPermission("enderenigma.info")) {
                        // logic for info
                    } else {
                        //Send no permission message
                    }
                    break;
                case "editor":
                    if (sender.hasPermission("enderenigma.editor")) {
                        // logic for editor
                    } else {
                        //Send no permission message
                    }
                    break;
                default:
                    // Send invalid command message
                    break;
            }
        } else if (sender.hasPermission("enderenigma.command")) {
            // Display help message
        } else {
            //Send no permission message
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("enderenigma.command") && args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("open");
            list.add("force-open");
            list.add("logs");
            list.add("info");
            list.add("editor");
            return list;
        }
        return null;
    }
}
