package com.cobelpvp.atheneum.nametag;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class NametagListener implements Listener {

    private TeamsNametagHandler nametags;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (TeamsNametagHandler.isInitiated()) {
            event.getPlayer().setMetadata("AtheneumNametag-LoggedIn", new FixedMetadataValue(Atheneum.getInstance(), true));
            TeamsNametagHandler.initiatePlayer(event.getPlayer());
            TeamsNametagHandler.reloadPlayer(event.getPlayer());
            TeamsNametagHandler.reloadOthersFor(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("AtheneumNametag-LoggedIn", Atheneum.getInstance());
        TeamsNametagHandler.getTeamMap().remove(event.getPlayer().getName());
    }
}
