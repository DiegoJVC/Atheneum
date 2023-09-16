package com.cobelpvp.atheneum.scoreboard;

import java.util.concurrent.ConcurrentHashMap;

import com.cobelpvp.atheneum.Atheneum;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import com.google.common.base.Preconditions;
import java.util.Map;

public class TeamsScoreboardHandler {

    @Getter
    private static Map<String, TeamsScoreboard> boards = new ConcurrentHashMap();
    @Getter
    private static ScoreboardConfiguration configuration = null;
    @Getter
    private static boolean initiated = false;
    @Getter
    private static int updateInterval = 2;

    public TeamsScoreboardHandler() {
    }

    public static void init() {
        if (Atheneum.getInstance().getConfig().getBoolean("disableScoreboard", false)) {
            return;
        }
        Preconditions.checkState(!initiated);
        initiated = true;
        new ScoreboardThread().start();
        Atheneum.getInstance().getServer().getPluginManager().registerEvents((Listener)new ScoreboardListener(), (Plugin) Atheneum.getInstance());
    }

    protected static void create(final Player player) {
        if (TeamsScoreboardHandler.configuration != null) {
            TeamsScoreboardHandler.boards.put(player.getName(), new TeamsScoreboard(player));
        }
    }

    protected static void updateScoreboard(final Player player) {
        TeamsScoreboard board = TeamsScoreboardHandler.boards.get(player.getName());
        if (board != null) {
            board.update();
        }
    }

    protected static void remove(final Player player) {
        TeamsScoreboardHandler.boards.remove(player.getName());
    }

    public static ScoreboardConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(final ScoreboardConfiguration configuration) {
        TeamsScoreboardHandler.configuration = configuration;
    }

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        TeamsScoreboardHandler.updateInterval = updateInterval;
    }
}
