package com.cobelpvp.atheneum.hologram.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HologramDeleteCommand {
    public HologramDeleteCommand() {
    }

    @Command(
            names = {"hologram delete", "holo delete"},
            permission = "proton.command.hologram.delete"
    )
    public static void execute(CommandSender sender, @Param(name = "hologram") Hologram hologram) {
        hologram.delete();
        sender.sendMessage(ChatColor.GOLD + "Deleted hologram with id " + ChatColor.WHITE + hologram.id() + ChatColor.GOLD + ".");
    }
}
