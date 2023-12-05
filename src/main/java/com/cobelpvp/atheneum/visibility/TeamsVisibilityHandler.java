package com.cobelpvp.atheneum.visibility;

import java.util.LinkedHashMap;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;
import java.util.Map;

public class TeamsVisibilityHandler
{
    private static final Map<String, VisibilityHandler> handlers;
    private static final Map<String, OverrideHandler> overrideHandlers;
    private static boolean initiated;

    public TeamsVisibilityHandler() {
    }

    public static void init() {
        Preconditions.checkState(!TeamsVisibilityHandler.initiated);
        TeamsVisibilityHandler.initiated = true;
        Bukkit.getPluginManager().registerEvents((Listener)new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerJoin(final PlayerJoinEvent event) {
                TeamsVisibilityHandler.update(event.getPlayer());
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onTabComplete(final PlayerChatTabCompleteEvent event) {
                final String token = event.getLastToken();
                final Collection<String> completions = event.getTabCompletions();
                completions.clear();
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    if (!TeamsVisibilityHandler.treatAsOnline(target, event.getPlayer())) {
                        continue;
                    }
                    if (!StringUtils.startsWithIgnoreCase(target.getName(), token)) {
                        continue;
                    }
                    completions.add(target.getName());
                }
            }
        }, (Plugin) Atheneum.getInstance());
    }

    public static void registerHandler(final String identifier, final VisibilityHandler handler) {
        TeamsVisibilityHandler.handlers.put(identifier, handler);
    }

    public static void registerOverride(final String identifier, final OverrideHandler handler) {
        TeamsVisibilityHandler.overrideHandlers.put(identifier, handler);
    }

    public static void update(final Player player) {
        if (TeamsVisibilityHandler.handlers.isEmpty() && TeamsVisibilityHandler.overrideHandlers.isEmpty()) {
            return;
        }
        updateAllTo(player);
        updateToAll(player);
    }

    @Deprecated
    public static void updateAllTo(final Player viewer) {
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            }
            else {
                viewer.showPlayer(target);
            }
        }
    }

    @Deprecated
    public static void updateToAll(final Player target) {
        for (final Player viewer : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            }
            else {
                viewer.showPlayer(target);
            }
        }
    }

    public static boolean treatAsOnline(final Player target, final Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("atheneum.staff");
    }

    private static boolean shouldSee(final Player target, final Player viewer) {
        for (final OverrideHandler handler : TeamsVisibilityHandler.overrideHandlers.values()) {
            if (handler.getAction(target, viewer) == OverrideAction.SHOW) {
                return true;
            }
        }
        for (final VisibilityHandler handler2 : TeamsVisibilityHandler.handlers.values()) {
            if (handler2.getAction(target, viewer) == VisibilityAction.HIDE) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getDebugInfo(final Player target, final Player viewer) {
        final List<String> debug = new ArrayList<String>();
        Boolean canSee = null;
        for (final Map.Entry<String, OverrideHandler> entry : TeamsVisibilityHandler.overrideHandlers.entrySet()) {
            final OverrideHandler handler = entry.getValue();
            final OverrideAction action = handler.getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action == OverrideAction.SHOW && canSee == null) {
                canSee = true;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Overriding Handler: \"" + entry.getKey() + "\": " + action);
        }
        for (final Map.Entry<String, VisibilityHandler> entry2 : TeamsVisibilityHandler.handlers.entrySet()) {
            final VisibilityHandler handler2 = entry2.getValue();
            final VisibilityAction action2 = handler2.getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action2 == VisibilityAction.HIDE && canSee == null) {
                canSee = false;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Normal Handler: \"" + entry2.getKey() + "\": " + action2);
        }
        if (canSee == null) {
            canSee = true;
        }
        debug.add(ChatColor.AQUA + "Result: " + viewer.getName() + " " + (canSee ? "can" : "cannot") + " see " + target.getName());
        return debug;
    }

    static {
        handlers = new LinkedHashMap<String, VisibilityHandler>();
        overrideHandlers = new LinkedHashMap<String, OverrideHandler>();
        TeamsVisibilityHandler.initiated = false;
    }
}
