package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.visibility.TeamsVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {
    @Override
    public OfflinePlayer transform(final CommandSender sender, final String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return (OfflinePlayer) sender;
        }
        return Atheneum.getInstance().getServer().getOfflinePlayer(source);
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
