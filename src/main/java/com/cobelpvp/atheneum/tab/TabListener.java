package com.cobelpvp.atheneum.tab;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TabListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        new BukkitRunnable() {
            public void run() {
                TeamsTabHandler.addPlayer(event.getPlayer());
            }
        }.runTaskLater(Atheneum.getInstance(), 10L);
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        TeamsTabHandler.removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }
}
