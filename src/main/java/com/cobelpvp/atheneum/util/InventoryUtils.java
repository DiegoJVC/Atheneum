package com.cobelpvp.atheneum.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static boolean fits(final ItemStack item, final Inventory target) {
        int leftToAdd = item.getAmount();
        if (target.getMaxStackSize() == Integer.MAX_VALUE) {
            return true;
        }
        for (final ItemStack itemStack : target.getContents()) {
            if (leftToAdd <= 0) {
                return true;
            }
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                leftToAdd -= item.getMaxStackSize();
            } else if (itemStack.isSimilar(item)) {
                leftToAdd -= itemStack.getMaxStackSize() - itemStack.getAmount();
            }
        }
        return leftToAdd <= 0;
    }
}
