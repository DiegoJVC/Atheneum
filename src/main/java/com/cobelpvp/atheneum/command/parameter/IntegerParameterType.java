package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class IntegerParameterType implements ParameterType<Integer> {
    @Override
    public Integer transform(final CommandSender sender, final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + value + " is not a valid number.");
            return null;
        }
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String prefix) {
        return ImmutableList.of();
    }
}
