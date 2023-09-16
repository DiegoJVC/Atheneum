package com.cobelpvp.atheneum.command.parameter.defaults;

import com.cobelpvp.atheneum.command.*;
import com.cobelpvp.atheneum.command.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandInfoCommand {
    @Command(names = {"cmdinfo"}, permission = "op", hidden = true)
    public static void commandInfo(final CommandSender sender, @Param(name = "command", wildcard = true) final String command) {
        final String[] args = command.split(" ");
        final ArgumentProcessor processor = new ArgumentProcessor();
        final Arguments arguments = processor.process(args);
        final CommandNode node = TeamsCommandHandler.ROOT_NODE.getCommand(arguments.getArguments().get(0));
        if (node != null) {
            final CommandNode realNode = node.findCommand(arguments);
            if (realNode != null) {
                final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(realNode.getOwningClass());
                sender.sendMessage(ChatColor.YELLOW + "Command '" + realNode.getFullLabel() + "' belongs to " + plugin.getName());
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + "Command not found.");
    }
}
