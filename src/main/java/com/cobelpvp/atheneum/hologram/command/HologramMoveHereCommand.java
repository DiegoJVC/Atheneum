package com.cobelpvp.atheneum.hologram.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HologramMoveHereCommand {
    public HologramMoveHereCommand() {
    }

    @Command(
            names = {"hologram movehere", "holo movehere", "hologram move", "holo move"},
            permission = "proton.command.hologram.movehere"
    )
    public static void execute(Player player, @Param(name = "hologram") Hologram hologram) {
        hologram.move(player.getLocation());
        player.sendMessage(ChatColor.GOLD + "Moved hologram with id " + ChatColor.WHITE + hologram.id() + ChatColor.GOLD + ".");
    }
}
