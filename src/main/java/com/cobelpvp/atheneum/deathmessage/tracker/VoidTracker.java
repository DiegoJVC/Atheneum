package com.cobelpvp.atheneum.deathmessage.tracker;

import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import com.cobelpvp.atheneum.deathmessage.damage.PlayerDamage;
import com.cobelpvp.atheneum.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.ChatColor;
import java.util.UUID;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Listener;

public final class VoidTracker implements Listener
{
    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        final List<Damage> record = TeamsDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (final Damage damage : record) {
            if (!(damage instanceof VoidDamage)) {
                if (damage instanceof VoidDamageByPlayer) {
                    continue;
                }
                if (!(damage instanceof PlayerDamage) || (knocker != null && damage.getTime() <= knockerTime)) {
                    continue;
                }
                knocker = damage;
                knockerTime = damage.getTime();
            }
        }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new VoidDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
        }
        else {
            event.setTrackerDamage(new VoidDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class VoidDamage extends Damage
    {
        public VoidDamage(final UUID damaged, final double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " fell into the void.";
        }
    }

    public static class VoidDamageByPlayer extends PlayerDamage
    {
        public VoidDamageByPlayer(final UUID damaged, final double damage, final UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " fell into the void thanks to " + Damage.wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }
    }
}
