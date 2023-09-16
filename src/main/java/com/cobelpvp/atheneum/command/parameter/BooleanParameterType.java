package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BooleanParameterType implements ParameterType<Boolean> {
    private final Map<String, Boolean> MAP;

    public BooleanParameterType() {
        (this.MAP = Maps.newHashMap()).put("true", true);
        this.MAP.put("on", true);
        this.MAP.put("yes", true);
        this.MAP.put("false", false);
        this.MAP.put("off", false);
        this.MAP.put("no", false);
    }

    @Override
    public Boolean transform(final CommandSender sender, final String source) {
        if (!this.MAP.containsKey(source.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid boolean.");
            return null;
        }
        return this.MAP.get(source.toLowerCase());
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String source) {
        return new ArrayList<String>(this.MAP.keySet());
    }
}
