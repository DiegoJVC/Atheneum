package com.cobelpvp.atheneum.command.parameter.filter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

abstract class BaseFilter implements ParameterType<String> {
    protected final Set<Pattern> bannedPatterns;

    BaseFilter() {
        this.bannedPatterns = new HashSet<Pattern>();
    }

    @Override
    public String transform(final CommandSender sender, final String value) {
        for (final Pattern bannedPattern : this.bannedPatterns) {
            if (bannedPattern.matcher(value).find()) {
                sender.sendMessage(ChatColor.RED + "Command contains inappropriate content.");
                return null;
            }
        }
        return value;
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String prefix) {
        return ImmutableList.of();
    }
}
