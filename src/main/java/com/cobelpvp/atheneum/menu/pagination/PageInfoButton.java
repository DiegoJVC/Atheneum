package com.cobelpvp.atheneum.menu.pagination;

import com.cobelpvp.atheneum.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PageInfoButton extends Button {

    private final PaginatedMenu menu;

    @Override
    public String getName(Player p0) {
        return ChatColor.GOLD + "Page Info";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();
        int pages = menu.getPages(player);
        lore.add(ChatColor.YELLOW + "You are viewing page #" + menu.getPage() + ".");
        lore.add(ChatColor.YELLOW + (pages == 1 ? "There is a 1 page." : "There are " + pages + " pages."));
        return lore;
    }

    @Override
    public Material getMaterial(Player p0) {
        return Material.PAPER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player);
            playNeutral(player);
        }
    }

}
