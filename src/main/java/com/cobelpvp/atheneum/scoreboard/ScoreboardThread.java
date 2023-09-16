package com.cobelpvp.atheneum.scoreboard;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.entity.Player;

final class ScoreboardThread extends Thread {

    public ScoreboardThread() {
        super("Atheneum - Scoreboard Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            for (final Player online : Atheneum.getInstance().getServer().getOnlinePlayers()) {
                try {
                    TeamsScoreboardHandler.updateScoreboard(online);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(TeamsScoreboardHandler.getUpdateInterval() * 50L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
}