package com.cobelpvp.atheneum.hologram.command;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.command.Command;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HologramListCommand {
    public HologramListCommand() {
    }

    @Command(
            names = {"hologram list", "holo list", "holograms", "holos"},
            permission = "proton.command.hologram.list"
    )
    public static void execute(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 48));
        Atheneum.getInstance().getHologramHandler().getCache().forEach((key, value) -> {
            (new FancyMessage(ChatColor.GRAY + "-> " + ChatColor.RED + key + "")).command("/tppos " + value.getLocation().getX() + " " + value.getLocation().getY() + " " + value.getLocation().getZ()).send(sender);
        });
        sender.sendMessage(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 48));
    }
}
