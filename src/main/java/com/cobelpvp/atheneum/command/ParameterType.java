package com.cobelpvp.atheneum.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface ParameterType<T> {

    T transform(CommandSender p0, String p1);

    List<String> tabComplete(Player p0, Set<String> p1, String p2);
}
