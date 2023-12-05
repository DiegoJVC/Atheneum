package com.cobelpvp.atheneum.hologram.command;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HologramAddLineCommand {
    public HologramAddLineCommand() {
    }

    @Command(
            names = {"hologram addline", "holo addline"},
            permission = "proton.command.hologram.addline"
    )
    public static void execute(CommandSender sender, @Param(name = "hologram") Hologram hologram, @Param(name = "text",wildcard = true) String text) {
        hologram.addLines(new String[]{ChatColor.translateAlternateColorCodes('&', text)});
    }
}
