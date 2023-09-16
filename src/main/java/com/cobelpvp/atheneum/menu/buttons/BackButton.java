package com.cobelpvp.atheneum.menu.buttons;

import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class BackButton extends Button {
    private final Menu back;

    @ConstructorProperties({"back"})
    public BackButton(final Menu back) {
        this.back = back;
    }

    @Override
    public Material getMaterial(final Player player) {
        return Material.REDSTONE;
    }

    @Override
    public byte getDamageValue(final Player player) {
        return 0;
    }

    @Override
    public String getName(final Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Back";
    }

    @Override
    public List<String> getDescription(final Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "Click here to return to");
        lore.add(ChatColor.RED + "the previous menu.");
        return lore;
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }
}
