package com.cobelpvp.atheneum.uuid;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

final class UUIDListener implements Listener {
    @EventHandler
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        TeamsUUIDCache.update(event.getUniqueId(), event.getName());
    }
}
