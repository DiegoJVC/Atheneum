package com.cobelpvp.atheneum.command.parameter.defaults;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BuildCommand {
    @Command(names = {"build"}, permission = "atheneum.build")
    public static void build(final Player sender) {
        if (sender.hasMetadata("Build")) {
            sender.removeMetadata("Build", Atheneum.getInstance());
        } else {
            sender.setMetadata("Build", new FixedMetadataValue(Atheneum.getInstance(), true));
        }

        sender.sendMessage(ChatColor.GOLD + "Your build mode have been " + (sender.hasMetadata("Build") ? "enabled" : "disabled"));
    }
}
