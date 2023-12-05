package com.cobelpvp.atheneum.hologram.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HologramSetLineCommand {
    public HologramSetLineCommand() {
    }

    @Command(
            names = {"hologram setline", "holo setline"},
            permission = "proton.command.hologram.setline"
    )
    public static void execute(CommandSender sender, @Param(name = "hologram") Hologram hologram, @Param(name = "number") int number, @Param(name = "text",wildcard = true) String text) {
        if (number > hologram.getLines().size()) {
            sender.sendMessage(ChatColor.RED + "This hologram does not have that many lines!");
        } else {
            hologram.setLine(number, text);
        }

    }
}
