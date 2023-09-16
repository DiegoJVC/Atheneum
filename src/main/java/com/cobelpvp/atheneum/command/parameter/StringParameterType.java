package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class StringParameterType implements ParameterType<String> {
    @Override
    public String transform(final CommandSender sender, final String value) {
        return value;
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String prefix) {
        return ImmutableList.of();
    }
}
