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

public final class BurnTracker implements Listener
{
    @EventHandler(priority = EventPriority.LOW)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FIRE_TICK && event.getCause().getCause() != EntityDamageEvent.DamageCause.LAVA) {
            return;
        }
        final List<Damage> record = TeamsDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (final Damage damage : record) {
            if (!(damage instanceof BurnDamage)) {
                if (damage instanceof BurnDamageByPlayer) {
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
            event.setTrackerDamage(new BurnDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
        }
        else {
            event.setTrackerDamage(new BurnDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class BurnDamage extends Damage
    {
        public BurnDamage(final UUID damaged, final double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " burned to death.";
        }
    }

    public static class BurnDamageByPlayer extends PlayerDamage
    {
        public BurnDamageByPlayer(final UUID damaged, final double damage, final UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " burned to death thanks to " + Damage.wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }
    }
}
