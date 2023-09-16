package com.cobelpvp.atheneum.menu.pagination;

import com.cobelpvp.atheneum.menu.Button;
import lombok.AllArgsConstructor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PageFilterButton<T> extends Button {

    private final FilterablePaginatedMenu<T> menu;

    @Override
    public String getName(Player p0) {
        return ChatColor.GRAY + "Filters";
    }

    @Override
    public List<String> getDescription(Player p0) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 10));

        for (PageFilter filter : menu.getFilters()) {
            String color;
            String decoration = "";
            String icon;

            if (filter.isEnabled()) {
                color = ChatColor.GREEN.toString();
                icon = StringEscapeUtils.unescapeJava("\u2713");
            } else {
                color = ChatColor.RED.toString();
                icon = StringEscapeUtils.unescapeJava("\u2717");
            }

            if (menu.getFilters().get(menu.getScrollIndex()).equals(filter)) {
                decoration = ChatColor.YELLOW + StringEscapeUtils.unescapeJava("ï¿½ ") + " ";
            }

            lore.add(decoration + color + icon + " " + filter.getName());
        }

        lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 10));
        lore.add("&eLeft click to scroll.");
        lore.add("&eRight click to toggle a filter.");
        lore.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 10));
        return lore;
    }

    @Override
    public Material getMaterial(Player p0) {
        if (menu.getFilters() == null || menu.getFilters().isEmpty()) {
            return Material.AIR;
        }
        return Material.HOPPER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (menu.getFilters() == null || menu.getFilters().isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no filters.");
        } else {
            if (clickType == ClickType.LEFT) {
                if (menu.getScrollIndex() == menu.getFilters().size() - 1) {
                    menu.setScrollIndex(0);
                } else {
                    menu.setScrollIndex(menu.getScrollIndex() + 1);
                }
            } else if (clickType == ClickType.RIGHT) {
                PageFilter<T> filter = menu.getFilters().get(menu.getScrollIndex());
                filter.setEnabled(!filter.isEnabled());
            }
        }
    }
}
