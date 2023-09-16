package com.cobelpvp.atheneum.util;

import com.cobelpvp.atheneum.Atheneum;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.*;

public class ItemUtils {
    private static final Map<String, ItemData> NAME_MAP;

    static {
        NAME_MAP = new HashMap<String, ItemData>();
    }

    public static void load() {
        ItemUtils.NAME_MAP.clear();
        final List<String> lines = readLines();
        for (final String line : lines) {
            final String[] parts = line.split(",");
            ItemUtils.NAME_MAP.put(parts[0], new ItemData(Material.getMaterial(Integer.parseInt(parts[1])), Short.parseShort(parts[2])));
        }
    }

    public static void setDisplayName(final ItemStack itemStack, final String name) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemBuilder builder(final Material type) {
        return new ItemBuilder(type);
    }

    public static ItemStack get(final String input, final int amount) {
        final ItemStack item = get(input);
        if (item != null) {
            item.setAmount(amount);
        }
        return item;
    }

    public static ItemStack get(String input) {
        input = input.toLowerCase().replace(" ", "");
        if (NumberUtils.isInteger(input)) {
            return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
        }
        if (input.contains(":")) {
            if (!NumberUtils.isShort(input.split(":")[1])) {
                return null;
            }
            if (NumberUtils.isInteger(input.split(":")[0])) {
                return new ItemStack(Material.getMaterial(Integer.parseInt(input.split(":")[0])), 1, Short.parseShort(input.split(":")[1]));
            }
            if (!ItemUtils.NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                return null;
            }
            final ItemData data = ItemUtils.NAME_MAP.get(input.split(":")[0].toLowerCase());
            return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
        } else {
            if (!ItemUtils.NAME_MAP.containsKey(input)) {
                return null;
            }
            return ItemUtils.NAME_MAP.get(input).toItemStack();
        }
    }

    public static String getName(final ItemStack item) {
        String name = CraftItemStack.asNMSCopy(item).getName();
        if (name.contains(".")) {
            name = WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "));
        }
        return name;
    }

    private static List<String> readLines() {
        try {
            return IOUtils.readLines(Atheneum.class.getClassLoader().getResourceAsStream("items.csv"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ItemData {
        private final Material material;
        private final short data;

        @ConstructorProperties({"material", "data"})
        public ItemData(final Material material, final short data) {
            this.material = material;
            this.data = data;
        }

        public String getName() {
            return ItemUtils.getName(this.toItemStack());
        }

        public boolean matches(final ItemStack item) {
            return item != null && item.getType() == this.material && item.getDurability() == this.data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(this.material, 1, this.data);
        }

        public Material getMaterial() {
            return this.material;
        }

        public short getData() {
            return this.data;
        }
    }

    public static final class ItemBuilder {
        private final Map<Enchantment, Integer> enchantments;
        private Material type;
        private int amount;
        private short data;
        private String name;
        private List<String> lore;

        private ItemBuilder(final Material type) {
            this.amount = 1;
            this.data = 0;
            this.lore = new ArrayList<String>();
            this.enchantments = new HashMap<Enchantment, Integer>();
            this.type = type;
        }

        public ItemBuilder type(final Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder data(final short data) {
            this.data = data;
            return this;
        }

        public ItemBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ItemBuilder addLore(final String... lore) {
            this.lore.addAll(Arrays.asList(lore));
            return this;
        }

        public ItemBuilder addLore(final int index, final String lore) {
            this.lore.set(index, lore);
            return this;
        }

        public ItemBuilder setLore(final List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder enchant(final Enchantment enchantment, final int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ItemBuilder unenchant(final Enchantment enchantment) {
            this.enchantments.remove(enchantment);
            return this;
        }

        public ItemStack build() {
            final ItemStack item = new ItemStack(this.type, this.amount, this.data);
            final ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
            final List<String> finalLore = new ArrayList<String>();
            for (int index = 0; index < this.lore.size(); ++index) {
                if (this.lore.get(index) == null) {
                    finalLore.set(index, "");
                } else {
                    finalLore.set(index, ChatColor.translateAlternateColorCodes('&', this.lore.get(index)));
                }
            }
            meta.setLore(finalLore);
            for (final Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
            item.setItemMeta(meta);
            return item;
        }
    }
}