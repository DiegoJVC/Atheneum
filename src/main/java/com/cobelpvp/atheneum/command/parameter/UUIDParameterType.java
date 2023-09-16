package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.visibility.TeamsVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UUIDParameterType implements ParameterType<UUID> {
    @Override
    public UUID transform(final CommandSender sender, final String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender).getUniqueId();
        }
        final UUID uuid = UUIDUtils.uuid(source);
        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + source + " has never joined the server.");
            return null;
        }
        return uuid;
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
