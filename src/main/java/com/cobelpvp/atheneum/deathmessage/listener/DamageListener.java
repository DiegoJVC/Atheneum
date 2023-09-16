package com.cobelpvp.atheneum.deathmessage.listener;

import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import com.cobelpvp.atheneum.deathmessage.damage.UnknownDamage;
import com.cobelpvp.atheneum.deathmessage.event.CustomPlayerDamageEvent;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Listener;

public final class DamageListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            final CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event);
            customEvent.setTrackerDamage(new UnknownDamage(player.getUniqueId(), customEvent.getDamage()));
            Atheneum.getInstance().getServer().getPluginManager().callEvent((Event)customEvent);
            TeamsDeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }
}
