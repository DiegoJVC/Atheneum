package com.cobelpvp.atheneum.serialization;

import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;
import com.mongodb.BasicDBList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.mongodb.BasicDBObject;

public final class ItemStackSerializer
{
    public static final BasicDBObject AIR = new BasicDBObject();

    private ItemStackSerializer() {
    }

    public static BasicDBObject serialize(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return ItemStackSerializer.AIR;
        }
        final BasicDBObject item = new BasicDBObject("type", itemStack.getType().toString()).append("amount", itemStack.getAmount()).append("data", itemStack.getDurability());
        final BasicDBList enchants = new BasicDBList();
        for (final Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchants.add(new BasicDBObject("enchantment", entry.getKey().getName()).append("level", entry.getValue()));
        }
        if (itemStack.getEnchantments().size() > 0) {
            item.append("enchants", enchants);
        }
        if (itemStack.hasItemMeta()) {
            final ItemMeta m = itemStack.getItemMeta();
            final BasicDBObject meta = new BasicDBObject("displayName", m.getDisplayName());
            if (m.getLore() != null) {}
            item.append("meta", meta);
        }
        return item;
    }

    public static ItemStack deserialize(final BasicDBObject dbObject) {
        if (dbObject == null || dbObject.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        final Material type = Material.valueOf(dbObject.getString("type"));
        final ItemStack item = new ItemStack(type, dbObject.getInt("amount"));
        item.setDurability(Short.parseShort(dbObject.getString("data")));
        if (dbObject.containsField("enchants")) {
            final BasicDBList enchs = (BasicDBList)dbObject.get("enchants");
            for (final Object o : enchs) {
                final BasicDBObject enchant = (BasicDBObject)o;
                item.addUnsafeEnchantment(Enchantment.getByName(enchant.getString("enchantment")), enchant.getInt("level"));
            }
        }
        if (dbObject.containsField("meta")) {
            final BasicDBObject meta = (BasicDBObject)dbObject.get("meta");
            final ItemMeta m = item.getItemMeta();
            if (meta.containsField("displayName")) {
                m.setDisplayName(meta.getString("displayName"));
            }
            if (meta.containsField("lore")) {
                m.setLore((List)new ArrayList<String>() {
                    private static final long serialVersionUID = -765088419932829612L;
                });
            }
            item.setItemMeta(m);
        }
        return item;
    }

    static {
        AIR.put("type", "AIR");
        AIR.put("amount", 1);
        AIR.put("data", 0);
    }
}
