package com.teenkung.enderenigma.manager;

import dev.lone.itemsadder.api.CustomStack;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemBuilder {
    private String type;
    private String id;
    private int amount;
    private int modelData;
    private ArrayList<String> lore;
    private String displayName;
    private Map<Enchantment, Integer> enchants;

    /**
     * get ItemBuilder class from ItemStack
     * @param stack ItemStack that you want to convert to ItemBuilder
     */
    public ItemBuilder(ItemStack stack) {
        CustomStack customStack = CustomStack.byItemStack(stack);
        if (customStack != null) {
            extractData(customStack.getItemStack(), "ItemsAdder", customStack.getNamespacedID());
        } else if (MMOItems.getType(stack) != null) {
            extractData(stack, MMOItems.getType(stack).toString(), MMOItems.getID(stack));
        } else {
            extractData(stack, "Vanilla", stack.getType().name());
        }
    }

    /**
     * get ItemBuilder class from ConfigurationSection
     * @param config ConfigurationSection that you want to convert to ItemBuilder, Must follow the format
     */
    public ItemBuilder(ConfigurationSection config) {
        this.type = config.getString("type", "Vanilla");
        this.id = config.getString("id", "");
        this.amount = config.getInt("amount", 1);

        if (this.type.equalsIgnoreCase("Vanilla") && Material.getMaterial(this.id) != null) {
            this.modelData = config.getInt("model-data", 0);
            this.lore = new ArrayList<>(config.getStringList("lore"));
            this.displayName = config.getString("name", null);
            ConfigurationSection section = config.getConfigurationSection("enchants");
            if (section != null) {
                this.enchants = getEnchants(section);
            }
        } else if (MMOItems.plugin.getItem(this.type, this.id) != null) {
            ItemStack stack = MMOItems.plugin.getItem(this.type, this.id);
            assert stack != null;
            extractData(stack, this.type, this.id);
        } else if (CustomStack.isInRegistry(this.id)) {
            ItemStack stack = CustomStack.getInstance(this.id).getItemStack();
            extractData(stack, "ItemsAdder", this.id);
        } else {
            throw new IllegalArgumentException("Invalid type or id provided");
        }
    }

    /**
     * Extract Data from ItemStack
     * @param stack ItemStack
     * @param type Item Type
     * @param id Item ID
     */
    private void extractData(ItemStack stack, String type, String id) {
        this.type = type;
        this.id = id;
        this.amount = stack.getAmount();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            this.modelData = meta.getCustomModelData();
            if (meta.getLore() != null) {
                this.lore = new ArrayList<>(meta.getLore());
            }
            this.displayName = meta.getDisplayName();
            this.enchants = meta.getEnchants();
        }
    }

    /**
     * Turn config section to Map of enchants
     * @param config config section of enchant as key and level as value
     * @return Map of all enchants in that config section
     */
    private Map<Enchantment, Integer> getEnchants(ConfigurationSection config) {
        Map<Enchantment, Integer> ench = new HashMap<>();
        for (String enchant : config.getKeys(false)) {
            ench.put(Enchantment.getByKey(NamespacedKey.fromString(enchant)), config.getInt(enchant, 0));
        }
        return ench;
    }


    /**
     * Build an Item from this class
     * @return built itemStack
     */
    private ItemStack buildItem() {
        if (type.equalsIgnoreCase("ItemsAdder")) {
            ItemStack stack = CustomStack.getInstance(id).getItemStack();
            return setData(stack);
        } else if (type.equalsIgnoreCase("Vanilla")) {
            ItemStack stack = MMOItems.plugin.getItem(this.type, this.id);
            if (stack == null) {
                return new ItemStack(Material.STONE);
            }
            return setData(stack);
        } else {
            Material mat = Material.getMaterial(id);
            if (mat == null) { mat = Material.STONE; }
            ItemStack stack = new ItemStack(mat);
            return setData(stack);
        }
    }

    /**
     * Set Data to target ItemStack
     * @param stack target ItemStack
     * @return edited ItemStack
     */
    private ItemStack setData(ItemStack stack) {
        stack.setAmount(amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (this.modelData != 0) {
                meta.setCustomModelData(this.modelData);
            }
            if (this.lore != null) {
                meta.setLore(this.lore);
            }
            if (this.displayName != null) {
                meta.setDisplayName(displayName);
            }
            if (this.enchants != null) {
                for (Enchantment key: this.enchants.keySet()) {
                    meta.addEnchant(key, this.enchants.get(key), true);
                }
            }
        }
        return stack;
    }

    public String getType() { return this.type; }
    public String getId() { return this.id; }
    public Integer getAmount() { return this.amount; }
    public String getDisplayName() { return this.displayName; }
    public ArrayList<String> getLore() { return this.lore; }
    public Integer getModelData() { return this.modelData; }
    public Map<Enchantment, Integer> getEnchants() { return this.enchants; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setLore(ArrayList<String> lore) { this.lore = lore; }
    public void setModelData(Integer modelData) { this.modelData = modelData; }
    public void addEnchant(Enchantment enchantment, Integer level) { this.enchants.put(enchantment, level); }
    public void removeEnchant(Enchantment enchantment) { this.enchants.remove(enchantment); }
    public ItemStack getItem() { return buildItem(); }
}