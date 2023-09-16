package com.cobelpvp.atheneum.command.bukkit;

import com.cobelpvp.atheneum.command.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.cobelpvp.atheneum.command.*;
import net.minecraft.util.org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.util.*;
import java.util.stream.Collectors;

public class TeamsCommand extends Command implements PluginIdentifiableCommand {

    protected CommandNode node;
    private final JavaPlugin owningPlugin;

    public TeamsCommand(CommandNode node, JavaPlugin plugin) {
        super(node.getName(), "", "/", Lists.newArrayList((Iterable) node.getRealAliases()));
        this.node = node;
        this.owningPlugin = plugin;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        label = label.replace(this.owningPlugin.getName().toLowerCase() + ":", "");
        String[] newArgs = this.concat(label, args);
        Arguments arguments = new ArgumentProcessor().process(newArgs);
        CommandNode executionNode = this.node.findCommand(arguments);
        String realLabel = this.getFullLabel(executionNode);
        if (executionNode.canUse(sender)) {
            if (executionNode.isAsync()) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            if (!executionNode.invoke(sender, arguments)) {
                                executionNode.getUsage(realLabel).send(sender);
                            }
                        } catch (CommandException ex) {
                            executionNode.getUsage(realLabel).send(sender);
                            sender.sendMessage(ChatColor.RED + "An error occurred while processing your command.");
                            if (sender.isOp()) {
                                TeamsCommand.this.sendStackTrace(sender, ex);
                            }
                        }
                    }
                }.runTaskAsynchronously(this.owningPlugin);
            } else {
                try {
                    if (!executionNode.invoke(sender, arguments)) {
                        executionNode.getUsage(realLabel).send(sender);
                    }
                } catch (CommandException ex) {
                    executionNode.getUsage(realLabel).send(sender);
                    sender.sendMessage(ChatColor.RED + "An error occurred while processing your command.");
                    if (sender.isOp()) {
                        this.sendStackTrace(sender, ex);
                    }
                }
            }
        } else if (executionNode.isHidden()) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
        } else {
            sender.sendMessage(TeamsCommandHandler.getConfig().getNoPermissionMessage());
        }
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        if (!(sender instanceof Player)) {
            return ImmutableList.of();
        } else {
            String[] rawArgs = cmdLine.replace(this.owningPlugin.getName().toLowerCase() + ":", "").split(" ");
            if (rawArgs.length < 1) {
                return !this.node.canUse(sender) ? ImmutableList.of() : ImmutableList.of();
            } else {
                Arguments arguments = (new ArgumentProcessor()).process(rawArgs);
                CommandNode realNode = this.node.findCommand(arguments);
                if (!realNode.canUse(sender)) {
                    return ImmutableList.of();
                } else {
                    List<String> realArgs = arguments.getArguments();
                    int currentIndex = realArgs.size() - 1;
                    if (currentIndex < 0) {
                        currentIndex = 0;
                    }

                    if (cmdLine.endsWith(" ") && realArgs.size() >= 1) {
                        ++currentIndex;
                    }

                    if (currentIndex < 0) {
                        return ImmutableList.of();
                    } else {
                        List<String> completions = new ArrayList();
                        if (realNode.hasCommands()) {
                            String name = realArgs.size() == 0 ? "" : realArgs.get(realArgs.size() - 1);
                            completions.addAll(realNode.getChildren().values().stream().filter((node) -> {
                                return node.canUse(sender) && (StringUtils.startsWithIgnoreCase(node.getName(), name) || StringUtils.isEmpty(name));
                            }).map(CommandNode::getName).collect(Collectors.toList()));
                            if (completions.size() > 0) {
                                return completions;
                            }
                        }

                        if (rawArgs[rawArgs.length - 1].equalsIgnoreCase(realNode.getName()) && !cmdLine.endsWith(" ")) {
                            return ImmutableList.of();
                        } else {
                            String argumentBeingCompleted;
                            if (realNode.getValidFlags() != null && !realNode.getValidFlags().isEmpty()) {
                                Iterator var16 = realNode.getValidFlags().iterator();

                                label102:
                                while (true) {
                                    String flags;
                                    do {
                                        do {
                                            if (!var16.hasNext()) {
                                                if (completions.size() > 0) {
                                                    return completions;
                                                }
                                                break label102;
                                            }

                                            flags = (String) var16.next();
                                            argumentBeingCompleted = rawArgs[rawArgs.length - 1];
                                        } while (!Flag.FLAG_PATTERN.matcher(argumentBeingCompleted).matches() && !argumentBeingCompleted.equals("-"));
                                    } while (!StringUtils.startsWithIgnoreCase(flags, argumentBeingCompleted.substring(1)) && !argumentBeingCompleted.equals("-"));

                                    completions.add("-" + flags);
                                }
                            }

                            try {
                                ParameterType<?> parameterType = null;
                                ParameterData data = null;
                                if (realNode.getParameters() != null) {
                                    List<ParameterData> params = realNode.getParameters().stream().filter((d) -> {
                                        return d instanceof ParameterData;
                                    }).map((d) -> {
                                        return (ParameterData) d;
                                    }).collect(Collectors.toList());
                                    int fixed = Math.max(0, currentIndex - 1);
                                    data = params.get(fixed);
                                    parameterType = TeamsCommandHandler.getParameterType(data.getType());
                                    if (data.getParameterType() != null) {
                                        try {
                                            parameterType = (ParameterType) data.getParameterType().newInstance();
                                        } catch (IllegalAccessException | InstantiationException var14) {
                                            var14.printStackTrace();
                                        }
                                    }
                                }

                                if (parameterType != null) {
                                    if (currentIndex < realArgs.size() && (realArgs.get(currentIndex)).equalsIgnoreCase(realNode.getName())) {
                                        realArgs.add("");
                                        ++currentIndex;
                                    }

                                    argumentBeingCompleted = currentIndex < realArgs.size() && realArgs.size() != 0 ? realArgs.get(currentIndex) : "";
                                    List<String> suggested = parameterType.tabComplete((Player) sender, data.getTabCompleteFlags(), argumentBeingCompleted);
                                    String finalArgumentBeingCompleted = argumentBeingCompleted;
                                    completions.addAll(suggested.stream().filter((s) -> {
                                        return StringUtils.startsWithIgnoreCase(s, finalArgumentBeingCompleted);
                                    }).collect(Collectors.toList()));
                                }
                            } catch (Exception var15) {
                            }

                            return completions;
                        }
                    }
                }
            }
        }
    }

    public Plugin getPlugin() {
        return this.owningPlugin;
    }

    private String[] concat(String label, String[] args) {
        String[] labelAsArray = {label};
        String[] newArgs = new String[args.length + 1];
        System.arraycopy(labelAsArray, 0, newArgs, 0, 1);
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }

    private String getFullLabel(CommandNode node) {
        final List<String> labels = new ArrayList<String>();
        while (node != null) {
            String name = node.getName();
            if (name != null) {
                labels.add(name);
            }
            node = node.getParent();
        }
        Collections.reverse(labels);
        labels.remove(0);
        StringBuilder builder = new StringBuilder();
        labels.forEach(s -> builder.append(s).append(' '));
        return builder.toString();
    }

    private void sendStackTrace(CommandSender sender, Exception exception) {
        String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
        sender.sendMessage(ChatColor.RED + "Message: " + rootCauseMessage);
        String cause = ExceptionUtils.getStackTrace(exception);
        StringTokenizer tokenizer = new StringTokenizer(cause);
        String exceptionType = "";
        String details = "";
        boolean parsingNeeded = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equalsIgnoreCase("Caused")) {
                tokenizer.nextToken();
                parsingNeeded = true;
                exceptionType = tokenizer.nextToken();
            } else {
                if (token.equalsIgnoreCase("at") && parsingNeeded) {
                    details = tokenizer.nextToken();
                    break;
                }
                continue;
            }
        }
        sender.sendMessage(ChatColor.RED + "Exception: " + exceptionType.replace(":", ""));
        sender.sendMessage(ChatColor.RED + "Details:");
        sender.sendMessage(ChatColor.RED + details);
    }

    public CommandNode getNode() {
        return this.node;
    }
}
