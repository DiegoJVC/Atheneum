package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.cobelpvp.atheneum.visibility.TeamsVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerParameterType implements ParameterType<Player> {
    @Override
    public Player transform(final CommandSender sender, final String value) {
        if (sender instanceof Player && (value.equalsIgnoreCase("self") || value.equals(""))) {
            return (Player) sender;
        }
        final Player player = Bukkit.getServer().getPlayer(value);
        if (player == null || (sender instanceof Player && !TeamsVisibilityHandler.treatAsOnline(player, (Player) sender))) {
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + value + "\" found.");
            return null;
        }
        return player;
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String source) {
        final List<String> completions = new ArrayList<String>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!TeamsVisibilityHandler.treatAsOnline(player, sender)) {
                continue;
            }
            completions.add(player.getName());
        }
        return completions;
    }
}
