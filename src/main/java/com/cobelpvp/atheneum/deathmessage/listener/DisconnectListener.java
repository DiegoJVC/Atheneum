package com.cobelpvp.atheneum.deathmessage.listener;

import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;

public final class DisconnectListener implements Listener
{
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        TeamsDeathMessageHandler.clearDamage(event.getPlayer());
    }
}
