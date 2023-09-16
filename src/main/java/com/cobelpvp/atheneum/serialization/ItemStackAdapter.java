package com.cobelpvp.atheneum.serialization;

import java.util.ArrayList;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.Color;
import java.util.List;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import java.util.Collection;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;

public class ItemStackAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack>
{
    public JsonElement serialize(final ItemStack item, final Type type, final JsonSerializationContext context) {
        return serialize(item);
    }

    public ItemStack deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        return deserialize(element);
    }

    public static JsonElement serialize(ItemStack item) {
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        final JsonObject element = new JsonObject();
        element.addProperty("id", (Number)item.getTypeId());
        element.addProperty(getDataKey(item), (Number)item.getDurability());
        element.addProperty("count", (Number)item.getAmount());
        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                element.addProperty("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                element.add("lore", (JsonElement)convertStringList(meta.getLore()));
            }
            if (meta instanceof LeatherArmorMeta) {
                element.addProperty("color", (Number)((LeatherArmorMeta)meta).getColor().asRGB());
            }
            else if (meta instanceof SkullMeta) {
                element.addProperty("skull", ((SkullMeta)meta).getOwner());
            }
            else if (meta instanceof BookMeta) {
                element.addProperty("title", ((BookMeta)meta).getTitle());
                element.addProperty("author", ((BookMeta)meta).getAuthor());
                element.add("pages", (JsonElement)convertStringList(((BookMeta)meta).getPages()));
            }
            else if (meta instanceof PotionMeta) {
                if (!((PotionMeta)meta).getCustomEffects().isEmpty()) {
                    element.add("potion-effects", (JsonElement)convertPotionEffectList(((PotionMeta)meta).getCustomEffects()));
                }
            }
            else if (meta instanceof MapMeta) {
                element.addProperty("scaling", Boolean.valueOf(((MapMeta)meta).isScaling()));
            }
            else if (meta instanceof EnchantmentStorageMeta) {
                final JsonObject storedEnchantments = new JsonObject();
                for (final Map.Entry<Enchantment, Integer> entry : ((EnchantmentStorageMeta)meta).getStoredEnchants().entrySet()) {
                    storedEnchantments.addProperty(entry.getKey().getName(), (Number)entry.getValue());
                }
                element.add("stored-enchants", (JsonElement)storedEnchantments);
            }
        }
        if (item.getEnchantments().size() != 0) {
            final JsonObject enchantments = new JsonObject();
            for (final Map.Entry<Enchantment, Integer> entry2 : item.getEnchantments().entrySet()) {
                enchantments.addProperty(entry2.getKey().getName(), (Number)entry2.getValue());
            }
            element.add("enchants", (JsonElement)enchantments);
        }
        return (JsonElement)element;
    }

    public static ItemStack deserialize(final JsonElement object) {
        if (object == null || !(object instanceof JsonObject)) {
            return new ItemStack(Material.AIR);
        }
        final JsonObject element = (JsonObject)object;
        final int id = element.get("id").getAsInt();
        final short data = (short)(element.has("damage") ? element.get("damage").getAsShort() : (element.has("data") ? element.get("data").getAsShort() : 0));
        final int count = element.get("count").getAsInt();
        final ItemStack item = new ItemStack(id, count, data);
        final ItemMeta meta = item.getItemMeta();
        if (element.has("name")) {
            meta.setDisplayName(element.get("name").getAsString());
        }
        if (element.has("lore")) {
            meta.setLore((List)convertStringList(element.get("lore")));
        }
        if (element.has("color")) {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(element.get("color").getAsInt()));
        }
        else if (element.has("skull")) {
            ((SkullMeta)meta).setOwner(element.get("skull").getAsString());
        }
        else if (element.has("title")) {
            ((BookMeta)meta).setTitle(element.get("title").getAsString());
            ((BookMeta)meta).setAuthor(element.get("author").getAsString());
            ((BookMeta)meta).setPages((List)convertStringList(element.get("pages")));
        }
        else if (element.has("potion-effects")) {
            final PotionMeta potionMeta = (PotionMeta)meta;
            for (final PotionEffect effect : convertPotionEffectList(element.get("potion-effects"))) {
                potionMeta.addCustomEffect(effect, false);
            }
        }
        else if (element.has("scaling")) {
            ((MapMeta)meta).setScaling(element.get("scaling").getAsBoolean());
        }
        else if (element.has("stored-enchants")) {
            final JsonObject enchantments = (JsonObject)element.get("stored-enchants");
            for (final Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment, enchantments.get(enchantment.getName()).getAsInt(), true);
                }
            }
        }
        item.setItemMeta(meta);
        if (element.has("enchants")) {
            final JsonObject enchantments = (JsonObject)element.get("enchants");
            for (final Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.getName()).getAsInt());
                }
            }
        }
        return item;
    }

    private static String getDataKey(final ItemStack item) {
        if (item.getType() == Material.AIR) {
            return "data";
        }
        if (Enchantment.DURABILITY.canEnchantItem(item)) {
            return "damage";
        }
        return "data";
    }

    public static JsonArray convertStringList(final Collection<String> strings) {
        final JsonArray ret = new JsonArray();
        for (final String string : strings) {
            ret.add((JsonElement)new JsonPrimitive(string));
        }
        return ret;
    }

    public static List<String> convertStringList(final JsonElement jsonElement) {
        final JsonArray array = jsonElement.getAsJsonArray();
        final List<String> ret = new ArrayList<String>();
        for (final JsonElement element : array) {
            ret.add(element.getAsString());
        }
        return ret;
    }

    public static JsonArray convertPotionEffectList(final Collection<PotionEffect> potionEffects) {
        final JsonArray ret = new JsonArray();
        for (final PotionEffect e : potionEffects) {
            ret.add((JsonElement)PotionEffectAdapter.toJson(e));
        }
        return ret;
    }

    public static List<PotionEffect> convertPotionEffectList(final JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        final JsonArray array = jsonElement.getAsJsonArray();
        final List<PotionEffect> ret = new ArrayList<PotionEffect>();
        for (final JsonElement element : array) {
            final PotionEffect e = PotionEffectAdapter.fromJson(element);
            if (e == null) {
                continue;
            }
            ret.add(e);
        }
        return ret;
    }

    public static class Key
    {
        public static final String ID = "id";
        public static final String COUNT = "count";
        public static final String NAME = "name";
        public static final String LORE = "lore";
        public static final String ENCHANTMENTS = "enchants";
        public static final String BOOK_TITLE = "title";
        public static final String BOOK_AUTHOR = "author";
        public static final String BOOK_PAGES = "pages";
        public static final String LEATHER_ARMOR_COLOR = "color";
        public static final String MAP_SCALING = "scaling";
        public static final String STORED_ENCHANTS = "stored-enchants";
        public static final String SKULL_OWNER = "skull";
        public static final String POTION_EFFECTS = "potion-effects";
    }
}
