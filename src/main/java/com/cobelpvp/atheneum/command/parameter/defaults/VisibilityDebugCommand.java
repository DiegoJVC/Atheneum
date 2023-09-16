package com.cobelpvp.atheneum.command.parameter.defaults;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.visibility.TeamsVisibilityHandler;
import net.minecraft.util.com.google.common.collect.Iterables;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class VisibilityDebugCommand {
    @Command(names = {"visibilitydebug", "debugvisibility", "visdebug", "cansee"}, permission = "")
    public static void visibilityDebug(final Player sender, @Param(name = "viewer") final Player viewer, @Param(name = "target") final Player target) {
        final List<String> lines = TeamsVisibilityHandler.getDebugInfo(target, viewer);
        for (final String debugLine : lines) {
            sender.sendMessage(debugLine);
        }
        boolean shouldBeAbleToSee = false;
        if (!((String) Iterables.getLast((Iterable) lines)).contains("cannot")) {
            shouldBeAbleToSee = true;
        }
        final boolean bukkit = viewer.canSee(target);
        if (shouldBeAbleToSee != bukkit) {
            sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Updating was not done correctly: " + viewer.getName() + " should be able to see " + target.getName() + " but cannot.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Bukkit currently respects this result.");
        }
    }
}
