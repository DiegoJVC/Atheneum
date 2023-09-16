package com.cobelpvp.atheneum.combatlogger;

import java.util.HashMap;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;
import java.util.UUID;
import java.util.Map;

public class TeamsCombatLoggerHandler {

    private static final Map<UUID, CombatLogger> combatLoggerMap = new HashMap<UUID, CombatLogger>();
    private static CombatLoggerConfiguration configuration = CombatLoggerConfiguration.DEFAULT_CONFIGURATION;
    private static boolean initiated = false;

    public static void init() {
        Preconditions.checkState(!TeamsCombatLoggerHandler.initiated);
        TeamsCombatLoggerHandler.initiated = true;
        Bukkit.getPluginManager().registerEvents(new CombatLoggerListener(), Atheneum.getInstance());
    }

    public static Map<UUID, CombatLogger> getCombatLoggerMap() {
        return TeamsCombatLoggerHandler.combatLoggerMap;
    }

    public static CombatLoggerConfiguration getConfiguration() {
        return TeamsCombatLoggerHandler.configuration;
    }

    public static void setConfiguration(final CombatLoggerConfiguration configuration) {
        TeamsCombatLoggerHandler.configuration = configuration;
    }

    static boolean isInitiated() {
        return TeamsCombatLoggerHandler.initiated;
    }
}
