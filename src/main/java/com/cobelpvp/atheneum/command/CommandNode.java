package com.cobelpvp.atheneum.command;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.NonNull;
import mkremins.fanciful.FancyMessage;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.beans.ConstructorProperties;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandNode {

    protected Method method;
    protected Class<?> owningClass;
    @NonNull
    private String name;
    private Set<String> aliases = new HashSet();
    @NonNull
    private String permission;
    private String description;
    private boolean async;
    private boolean hidden;
    private List<String> validFlags;
    private List<Data> parameters;
    private Map<String, CommandNode> children = new TreeMap();
    private CommandNode parent;
    private boolean logToConsole;

    public CommandNode(Class<?> owningClass) {
        this.owningClass = owningClass;
    }

    @ConstructorProperties({"name", "permission"})
    public CommandNode(@NonNull String name, @NonNull String permission) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.name = name;
            this.permission = permission;
        }
    }

    @ConstructorProperties({"name", "aliases", "permission", "description", "async", "hidden", "method", "owningClass", "validFlags", "parameters", "children", "parent", "logToConsole"})
    public CommandNode(@NonNull String name, Set<String> aliases, @NonNull String permission, String description, boolean async, boolean hidden, Method method, Class<?> owningClass, List<String> validFlags, List<Data> parameters, Map<String, CommandNode> children, CommandNode parent, boolean logToConsole) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.name = name;
            this.aliases = aliases;
            this.permission = permission;
            this.description = description;
            this.async = async;
            this.hidden = hidden;
            this.method = method;
            this.owningClass = owningClass;
            this.validFlags = validFlags;
            this.parameters = parameters;
            this.children = children;
            this.parent = parent;
            this.logToConsole = logToConsole;
        }
    }

    public CommandNode() {
    }

    public void registerCommand(CommandNode commandNode) {
        commandNode.setParent(this);
        this.children.put(commandNode.getName(), commandNode);
    }

    public boolean hasCommand(String name) {
        return this.children.containsKey(name.toLowerCase());
    }

    public CommandNode getCommand(String name) {
        return this.children.get(name.toLowerCase());
    }

    public boolean hasCommands() {
        return this.children.size() > 0;
    }

    public CommandNode findCommand(Arguments arguments) {
        if (arguments.getArguments().size() > 0) {
            String trySub = arguments.getArguments().get(0);
            if (this.hasCommand(trySub)) {
                arguments.getArguments().remove(0);
                CommandNode returnNode = this.getCommand(trySub);
                return returnNode.findCommand(arguments);
            }
        }

        return this;
    }

    public boolean isValidFlag(String test) {
        return test.length() == 1 ? this.validFlags.contains(test) : this.validFlags.contains(test.toLowerCase());
    }

    public boolean canUse(final CommandSender sender) {
        if (this.permission == null) {
            return true;
        }
        String permission = this.permission;
        switch (permission) {
            case "console": {
                return sender instanceof ConsoleCommandSender;
            }
            case "op": {
                return sender.isOp();
            }
            case "": {
                return true;
            }
            default: {
                return sender.hasPermission(this.permission);
            }
        }
    }

    public FancyMessage getUsage(String realLabel) {
        FancyMessage usage = (new FancyMessage("Correct usage: /" + realLabel)).color(ChatColor.RED);
        if (!Strings.isNullOrEmpty(this.getDescription())) {
            usage.tooltip(ChatColor.YELLOW + this.getDescription());
        }

        List<FlagData> flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter((datax) -> {
            return datax instanceof FlagData;
        }).map((datax) -> {

            return (FlagData) datax;
        }).collect(Collectors.toList()));
        List<ParameterData> parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter((datax) -> {
            return datax instanceof ParameterData;
        }).map((datax) -> {
            return (ParameterData) datax;
        }).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(this.getDescription())) {
                usage.tooltip(ChatColor.YELLOW + this.getDescription());
            }

            Iterator var6 = flags.iterator();

            while (var6.hasNext()) {
                FlagData data = (FlagData) var6.next();
                String name = data.getNames().get(0);
                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                    if (!Strings.isNullOrEmpty(this.getDescription())) {
                        usage.tooltip(ChatColor.YELLOW + this.getDescription());
                    }
                }

                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);
                if (!Strings.isNullOrEmpty(data.getDescription())) {
                    usage.tooltip(ChatColor.GRAY + data.getDescription());
                }
            }

            usage.then(") ").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(this.getDescription())) {
                usage.tooltip(ChatColor.YELLOW + this.getDescription());
            }
        }

        if (!parameters.isEmpty()) {
            for (int index = 0; index < parameters.size(); ++index) {
                ParameterData data = parameters.get(index);
                boolean required = data.getDefaultValue().isEmpty();
                usage.then((required ? "<" : "[") + data.getName() + (data.isWildcard() ? "..." : "") + (required ? ">" : "]") + (index != parameters.size() - 1 ? " " : "")).color(ChatColor.RED);
                if (!Strings.isNullOrEmpty(this.getDescription())) {
                    usage.tooltip(ChatColor.YELLOW + this.getDescription());
                }
            }
        }

        return usage;
    }

    public FancyMessage getUsage() {
        FancyMessage usage = new FancyMessage("");
        List<FlagData> flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter((datax) -> {
            return datax instanceof FlagData;
        }).map((datax) -> {
            return (FlagData) datax;
        }).collect(Collectors.toList()));
        List<ParameterData> parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter((datax) -> {
            return datax instanceof ParameterData;
        }).map((datax) -> {
            return (ParameterData) datax;
        }).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);

            for (FlagData data : flags) {
                String name = data.getNames().get(0);
                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                }

                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);
                if (!Strings.isNullOrEmpty(data.getDescription())) {
                    usage.tooltip(ChatColor.GRAY + data.getDescription());
                }
            }

            usage.then(") ").color(ChatColor.RED);
        }

        if (!parameters.isEmpty()) {
            for (int index = 0; index < parameters.size(); ++index) {
                ParameterData data = parameters.get(index);
                boolean required = data.getDefaultValue().isEmpty();
                usage.then((required ? "<" : "[") + data.getName() + (data.isWildcard() ? "..." : "") + (required ? ">" : "]") + (index != parameters.size() - 1 ? " " : "")).color(ChatColor.RED);
            }
        }

        return usage;
    }

    public boolean invoke(CommandSender sender, Arguments arguments) throws CommandException {
        if (this.method == null) {
            if (this.hasCommands()) {
                if (this.getSubCommands(sender, true).isEmpty()) {
                    if (this.isHidden()) {
                        sender.sendMessage(SpigotConfig.unknownCommandMessage);
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions to perform this command.");
                    }
                }
            } else {
                sender.sendMessage(SpigotConfig.unknownCommandMessage);
            }

            return true;
        } else {
            List<Object> objects = new ArrayList(this.method.getParameterCount());
            objects.add(sender);
            int index = 0;
            Iterator var5 = this.parameters.iterator();

            while (true) {
                while (var5.hasNext()) {
                    Data unknownData = (Data) var5.next();
                    if (unknownData instanceof FlagData) {
                        FlagData flagData = (FlagData) unknownData;
                        boolean value = flagData.getDefaultValue();

                        for (String s : flagData.getNames()) {
                            if (arguments.hasFlag(s)) {
                                value = !value;
                                break;
                            }
                        }

                        objects.add(flagData.getMethodIndex(), value);
                    } else if (unknownData instanceof ParameterData) {
                        ParameterData parameterData = (ParameterData) unknownData;

                        String argument;
                        try {
                            argument = arguments.getArguments().get(index);
                        } catch (Exception var13) {
                            if (parameterData.getDefaultValue().isEmpty()) {
                                return false;
                            }

                            argument = parameterData.getDefaultValue();
                        }

                        if (parameterData.isWildcard() && (argument.isEmpty() || !argument.equals(parameterData.getDefaultValue()))) {
                            argument = arguments.join(index);
                        }

                        ParameterType<?> type = TeamsCommandHandler.getParameterType(parameterData.getType());
                        if (parameterData.getParameterType() != null) {
                            try {
                                type = (ParameterType) parameterData.getParameterType().newInstance();
                            } catch (IllegalAccessException | InstantiationException var12) {
                                var12.printStackTrace();
                                throw new CommandException("Failed to create ParameterType instance: " + parameterData.getParameterType().getName(), var12);
                            }
                        }

                        if (type == null) {
                            Class<?> t = parameterData.getParameterType() == null ? parameterData.getType() : parameterData.getParameterType();
                            sender.sendMessage(ChatColor.RED + "No parameter type found: " + t.getSimpleName());
                            return true;
                        }

                        Object result = type.transform(sender, argument);
                        if (result == null) {
                            return true;
                        }

                        objects.add(parameterData.getMethodIndex(), result);
                        ++index;
                    }
                }

                try {
                    Stopwatch stopwatch = new Stopwatch();
                    stopwatch.start();
                    method.invoke(null, objects.toArray());
                    stopwatch.stop();
                    int executionThreshold = Atheneum.getInstance().getConfig().getInt("Command.TimeThreshold", 10);
                    if (!this.async && this.logToConsole && stopwatch.elapsedMillis() >= (long) executionThreshold) {
                    }

                    return true;
                } catch (InvocationTargetException | IllegalAccessException var11) {
                    var11.printStackTrace();
                    throw new CommandException("An error occurred while executing the command", var11);
                }
            }
        }
    }

    public List<String> getSubCommands(CommandSender sender, boolean print) {
        List<String> commands = new ArrayList();
        if (this.canUse(sender)) {
            String command = (sender instanceof Player ? "/" : "") + this.getFullLabel() + (this.parameters != null ? " " + this.getUsage().toOldMessageFormat() : "") + (!Strings.isNullOrEmpty(this.description) ? ChatColor.GRAY + " - " + this.getDescription() : "");
            if (this.parent == null) {
                commands.add(command);
            } else if (this.parent.getName() != null && TeamsCommandHandler.ROOT_NODE.getCommand(this.parent.getName()) != this.parent) {
                commands.add(command);
            }

            if (this.hasCommands()) {

                for (CommandNode n : this.getChildren().values()) {
                    commands.addAll(n.getSubCommands(sender, false));
                }
            }
        }

        if (!commands.isEmpty() && print) {
            for (String command : commands) {
                sender.sendMessage(ChatColor.RED + command);
            }
        }

        return commands;
    }

    public Set<String> getRealAliases() {
        Set<String> aliases = this.getAliases();
        aliases.remove(this.getName());
        return aliases;
    }

    public String getFullLabel() {
        List<String> labels = new ArrayList();

        for (CommandNode node = this; node != null; node = node.getParent()) {
            String name = node.getName();
            if (name != null) {
                labels.add(name);
            }
        }

        Collections.reverse(labels);
        labels.remove(0);
        StringBuilder builder = new StringBuilder();
        labels.forEach((s) -> {
            builder.append(s).append(' ');
        });
        return builder.toString().trim();
    }

    public String getUsageForHelpTopic() {
        return this.method != null && this.parameters != null ? "/" + this.getFullLabel() + " " + ChatColor.stripColor(this.getUsage().toOldMessageFormat()) : "";
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name");
        } else {
            this.name = name;
        }
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    @NonNull
    public String getPermission() {
        return this.permission;
    }

    public void setPermission(@NonNull String permission) {
        if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.permission = permission;
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAsync() {
        return this.async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getOwningClass() {
        return this.owningClass;
    }

    public List<String> getValidFlags() {
        return this.validFlags;
    }

    public void setValidFlags(List<String> validFlags) {
        this.validFlags = validFlags;
    }

    public List<Data> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<Data> parameters) {
        this.parameters = parameters;
    }

    public Map<String, CommandNode> getChildren() {
        return this.children;
    }

    public CommandNode getParent() {
        return this.parent;
    }

    public void setParent(CommandNode parent) {
        this.parent = parent;
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }
}