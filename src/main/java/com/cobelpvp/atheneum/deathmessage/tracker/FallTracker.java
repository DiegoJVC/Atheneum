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

public final class FallTracker implements Listener
{
    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        final List<Damage> record = TeamsDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (final Damage damage : record) {
            if (!(damage instanceof FallDamage)) {
                if (damage instanceof FallDamageByPlayer) {
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
            event.setTrackerDamage(new FallDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
        }
        else {
            event.setTrackerDamage(new FallDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class FallDamage extends Damage
    {
        public FallDamage(final UUID damaged, final double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " hit the ground too hard.";
        }
    }

    public static class FallDamageByPlayer extends PlayerDamage
    {
        public FallDamageByPlayer(final UUID damaged, final double damage, final UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " hit the ground too hard thanks to " + Damage.wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }
    }
}
