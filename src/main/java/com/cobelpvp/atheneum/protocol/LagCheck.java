package com.cobelpvp.atheneum.protocol;

import com.cobelpvp.atheneum.util.PlayerUtils;
import com.cobelpvp.atheneum.protocol.event.ServerLaggedOutEvent;
import org.bukkit.entity.Player;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class LagCheck extends BukkitRunnable
{
    public void run() {
        final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        if (players.size() >= 100) {
            int playersLagging = 0;
            for (Player player : players) {
                if (PlayerUtils.isLagging(player)) {
                    ++playersLagging;
                }
            }
            final double percentage = playersLagging * 100 / players.size();
            if (Math.abs(percentage) >= 30.0) {
                Bukkit.getPluginManager().callEvent(new ServerLaggedOutEvent(PingAdapter.getAveragePing()));
            }
        }
    }
}
