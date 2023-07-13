package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SubPoolConfig {

    private final ConfigurationSection section;
    private final EnderEnigma plugin;
    private final ItemBuilder item;
    private final Integer weight;
    private final ArrayList<String> commands;
    private final boolean giveItem;


    public SubPoolConfig(ConfigurationSection section, EnderEnigma plugin) {
        this.section = section;
        this.plugin = plugin;
        ConfigurationSection itemSection = section.getConfigurationSection("item");
        if (itemSection == null) {
            try {
                throw new InvalidConfigurationException("Item must not be null.");
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.item = new ItemBuilder(itemSection);
            this.weight = section.getInt("weight");
            this.commands = new ArrayList<>(section.getStringList("commands"));
            this.giveItem = section.getBoolean("give-item");
        }
    }

    public Integer getWeight() { return weight; }
    public ArrayList<String> getCommands() { return commands; }
    public ItemStack getItem() { return item.getItem(); }
    public Boolean getGiveItem() { return giveItem; }

}
