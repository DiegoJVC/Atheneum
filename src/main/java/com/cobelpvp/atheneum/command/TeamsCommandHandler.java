package com.cobelpvp.atheneum.command;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.command.bukkit.TeamsCommand;
import com.cobelpvp.atheneum.command.bukkit.TeamsCommandMap;
import com.cobelpvp.atheneum.command.bukkit.TeamsHelpTopic;
import com.cobelpvp.atheneum.command.parameter.*;
import com.cobelpvp.atheneum.command.parameter.*;
import com.cobelpvp.atheneum.command.parameter.defaults.BuildCommand;
import com.cobelpvp.atheneum.command.parameter.defaults.CommandInfoCommand;
import com.cobelpvp.atheneum.command.parameter.defaults.EvalCommand;
import com.cobelpvp.atheneum.command.parameter.defaults.VisibilityDebugCommand;
import com.cobelpvp.atheneum.command.parameter.filter.NormalFilter;
import com.cobelpvp.atheneum.command.parameter.filter.StrictFilter;
import com.cobelpvp.atheneum.command.parameter.offline.OfflinePlayerWrapper;
import com.cobelpvp.atheneum.command.parameter.offline.OfflinePlayerWrapperParameterType;
import com.cobelpvp.atheneum.command.utils.EasyClass;
import com.cobelpvp.atheneum.util.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class TeamsCommandHandler {

    public static CommandNode ROOT_NODE;
    protected static Map<Class<?>, ParameterType<?>> PARAMETER_TYPE_MAP;
    protected static CommandMap commandMap;
    protected static Map<String, Command> knownCommands;
    private static CommandConfiguration config;

    static {
        ROOT_NODE = new CommandNode();
        PARAMETER_TYPE_MAP = new HashMap<Class<?>, ParameterType<?>>();
        config = new CommandConfiguration().setNoPermissionMessage(SpigotConfig.unknownCommandMessage);
        registerParameterType(Boolean.TYPE, new BooleanParameterType());
        registerParameterType(Integer.TYPE, new IntegerParameterType());
        registerParameterType(Double.TYPE, new DoubleParameterType());
        registerParameterType(Float.TYPE, new FloatParameterType());
        registerParameterType(String.class, new StringParameterType());
        registerParameterType(Player.class, new PlayerParameterType());
        registerParameterType(World.class, new WorldParameterType());
        registerParameterType(ItemStack.class, new ItemStackParameterType());
        registerParameterType(OfflinePlayer.class, new OfflinePlayerParameterType());
        registerParameterType(UUID.class, new UUIDParameterType());
        registerParameterType(OfflinePlayerWrapper.class, new OfflinePlayerWrapperParameterType());
        registerParameterType(NormalFilter.class, new NormalFilter());
        registerParameterType(StrictFilter.class, new StrictFilter());
        commandMap = getCommandMap();
        knownCommands = getKnownCommands();
    }

    public static void init() {
        registerClass(BuildCommand.class);
        registerClass(EvalCommand.class);
        registerClass(CommandInfoCommand.class);
        registerClass(VisibilityDebugCommand.class);
        new BukkitRunnable() {
            public void run() {
                try {
                    swapCommandMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(Atheneum.getInstance(), 5L);
    }

    public static void registerParameterType(Class<?> clazz, ParameterType<?> type) {
        PARAMETER_TYPE_MAP.put(clazz, type);
    }

    public static ParameterType getParameterType(Class<?> clazz) {
        return PARAMETER_TYPE_MAP.get(clazz);
    }

    public static CommandConfiguration getConfig() {
        return config;
    }

    public static void setConfig(CommandConfiguration config) {
        TeamsCommandHandler.config = config;
    }

    public static void registerMethod(Method method) {
        method.setAccessible(true);
        Set<CommandNode> nodes = (new MethodProcessor()).process(method);
        if (nodes != null) {
            nodes.forEach((node) -> {
                if (node != null) {
                    TeamsCommand command = new TeamsCommand(node, JavaPlugin.getProvidingPlugin(method.getDeclaringClass()));
                    register(command);
                    node.getChildren().values().forEach((n) -> {
                        registerHelpTopic(n, node.getAliases());
                    });
                }

            });
        }

    }

    protected static void registerHelpTopic(CommandNode node, Set<String> aliases) {
        if (node.method != null) {
            Bukkit.getHelpMap().addTopic(new TeamsHelpTopic(node, aliases));
        }
        if (node.hasCommands()) {
            node.getChildren().values().forEach(n -> registerHelpTopic(n, null));
        }
    }

    private static void register(TeamsCommand command) {
        try {
            Map<String, Command> knownCommands = getKnownCommands();
            Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Command> entry = iterator.next();
                if (entry.getValue().getName().equalsIgnoreCase(command.getName())) {
                    entry.getValue().unregister(commandMap);
                    iterator.remove();
                }
            }
            for (String alias : command.getAliases()) {
                knownCommands.put(alias, command);
            }
            command.register(commandMap);
            knownCommands.put(command.getName(), command);
        } catch (Exception ex) {
        }
    }

    public static void registerClass(Class<?> clazz) {
        for (final Method method : clazz.getMethods()) {
            registerMethod(method);
        }
    }

    public static void unregisterClass(Class<?> clazz) {
        final Map<String, Command> knownCommands = getKnownCommands();
        final Iterator<Command> iterator = knownCommands.values().iterator();
        while (iterator.hasNext()) {
            final Command command = iterator.next();
            if (!(command instanceof TeamsCommand)) {
                continue;
            }
            final CommandNode node = ((TeamsCommand) command).getNode();
            if (node.getOwningClass() != clazz) {
                continue;
            }
            command.unregister(commandMap);
            iterator.remove();
        }
    }

    public static void registerPackage(Plugin plugin, String packageName) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(TeamsCommandHandler::registerClass);
    }

    public static void registerAll(Plugin plugin) {
        registerPackage(plugin, plugin.getClass().getPackage().getName());
    }

    private static void swapCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        Object oldCommandMap = commandMapField.get(Bukkit.getServer());
        TeamsCommandMap newCommandMap = new TeamsCommandMap(Bukkit.getServer());
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & 0xFFFFFFEF);
        knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));
        commandMapField.set(Bukkit.getServer(), newCommandMap);
    }

    protected static CommandMap getCommandMap() {
        return new EasyClass<Server>(Bukkit.getServer()).<CommandMap>getField("commandMap").get();
    }

    protected static Map<String, Command> getKnownCommands() {
        return new EasyClass<CommandMap>(commandMap).<Map<String, Command>>getField("knownCommands").get();
    }
}