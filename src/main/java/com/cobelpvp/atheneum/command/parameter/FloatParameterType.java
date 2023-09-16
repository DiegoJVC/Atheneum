package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class FloatParameterType implements ParameterType<Float> {
    @Override
    public Float transform(final CommandSender sender, final String value) {
        if (value.toLowerCase().contains("e")) {
            sender.sendMessage(ChatColor.RED + value + " is not a valid number.");
            return null;
        }
        try {
            final float parsed = Float.parseFloat(value);
            if (Float.isNaN(parsed) || !Float.isFinite(parsed)) {
                sender.sendMessage(ChatColor.RED + value + " is not a valid number.");
                return null;
            }
            return parsed;
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
