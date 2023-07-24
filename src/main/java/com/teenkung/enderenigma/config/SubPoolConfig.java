package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SubPoolConfig {
    private final String id;
    private ItemBuilder item;
    private Integer weight;
    private ArrayList<String> commands;
    private Boolean giveItem;


    public SubPoolConfig(ConfigurationSection section, EnderEnigma plugin) {
        ConfigurationSection itemSection = section.getConfigurationSection("item");
        if (itemSection == null) {
            try {
                throw new InvalidConfigurationException("Item must not be null.");
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.id = section.getName();
            this.item = new ItemBuilder(itemSection);
            this.weight = section.getInt("weight");
            this.commands = new ArrayList<>(section.getStringList("commands"));
            this.giveItem = section.getBoolean("give-item");
        }
    }

    public SubPoolConfig(String id, ItemBuilder item, Integer weight, ArrayList<String> commands, Boolean giveItem) {
        this.id = id;
        this.item = item;
        this.weight = weight;
        this.commands = commands;
        this.giveItem = giveItem;
    }



    public String getId() { return id; }
    public ItemStack getItem() { return item.getItem(); }
    public Integer getWeight() { return weight; }
    public ArrayList<String> getCommands() { return commands; }
    public Boolean getGiveItem() { return giveItem; }

    public void setItem(ItemBuilder item) { this.item = item; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public void setCommands(ArrayList<String> commands) { this.commands = commands; }
    public void setGiveItem(Boolean status) { this.giveItem = status; }

}
