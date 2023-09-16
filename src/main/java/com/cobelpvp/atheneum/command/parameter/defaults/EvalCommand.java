package com.cobelpvp.atheneum.command.parameter.defaults;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class EvalCommand {
    @Command(names = {"eval"}, permission = "console", description = "Evaluates a command")
    public static void eval(final CommandSender sender, @Param(name = "command", wildcard = true) final String commandLine) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This is a console-only utility command. It cannot be used from in-game.");
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
    }
}
