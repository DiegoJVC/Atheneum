package com.cobelpvp.atheneum.scoreboard;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

final class ScoreboardListener implements Listener {
    ScoreboardListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TeamsScoreboardHandler.create(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TeamsScoreboardHandler.remove(event.getPlayer());
    }
}

