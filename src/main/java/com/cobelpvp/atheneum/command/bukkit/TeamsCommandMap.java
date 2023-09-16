package com.cobelpvp.atheneum.command.bukkit;

import com.cobelpvp.atheneum.command.CommandNode;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TeamsCommandMap extends SimpleCommandMap {

    public TeamsCommandMap(Server server) {
        super(server);
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");
        int spaceIndex = cmdLine.indexOf(32);
        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList<String>();
            Map<String, Command> knownCommands = this.knownCommands;
            String prefix = (sender instanceof Player) ? "/" : "";
            for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                final String name = commandEntry.getKey();
                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    Command command = commandEntry.getValue();
                    if (command instanceof TeamsCommand) {
                        CommandNode executionNode = ((TeamsCommand) command).node.getCommand(name);
                        if (executionNode == null) {
                            executionNode = ((TeamsCommand) command).node;
                        }
                        if (!executionNode.hasCommands()) {
                            CommandNode testNode = executionNode.getCommand(name);
                            if (testNode == null) {
                                testNode = ((TeamsCommand) command).node.getCommand(name);
                            }
                            if (!testNode.canUse(sender)) {
                                continue;
                            }
                            completions.add(prefix + name);
                        } else {
                            if (executionNode.getSubCommands(sender, false).size() == 0) {
                                continue;
                            }
                            completions.add(prefix + name);
                        }
                    } else {
                        if (!command.testPermissionSilent(sender)) {
                            continue;
                        }
                        completions.add(prefix + name);
                    }
                }
            }
            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        }
        String commandName = cmdLine.substring(0, spaceIndex);
        Command target = this.getCommand(commandName);
        if (target == null) {
            return null;
        }
        if (!target.testPermissionSilent(sender)) {
            return null;
        }
        String argLine = cmdLine.substring(spaceIndex + 1);
        String[] args = argLine.split(" ");
        try {
            List<String> completions2 = (target instanceof TeamsCommand) ? ((TeamsCommand) target).tabComplete(sender, cmdLine) : target.tabComplete(sender, commandName, args);
            if (completions2 != null) {
                Collections.sort(completions2, String.CASE_INSENSITIVE_ORDER);
            }
            return completions2;
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex2) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex2);
        }
    }
}