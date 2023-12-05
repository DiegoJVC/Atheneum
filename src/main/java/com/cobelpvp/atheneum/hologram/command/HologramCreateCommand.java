package com.cobelpvp.atheneum.hologram.command;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HologramCreateCommand {
    public HologramCreateCommand() {
    }

    @Command(
            names = {"hologram create", "holo create"},
            permission = "proton.command.hologram.create"
    )
    public static void execute(Player player, @Param(name = "id") int id, @Param(name = "text",wildcard = true) String text) {
        if (Atheneum.getInstance().getHologramHandler().fromId(id) != null) {
            player.sendMessage(ChatColor.RED + "Hologram with id " + ChatColor.WHITE + id + ChatColor.RED + " already exists.");
        } else {
            Hologram hologram = Atheneum.getInstance().getHologramHandler().createHologram().addLines(new String[]{text}).at(player.getLocation()).build();
            hologram.send();
            player.sendMessage(ChatColor.GOLD + "Created new hologram with id " + ChatColor.WHITE + id + ChatColor.GOLD + ".");
        }

    }
}
