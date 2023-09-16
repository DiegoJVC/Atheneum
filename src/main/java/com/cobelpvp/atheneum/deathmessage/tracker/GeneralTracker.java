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
import org.bukkit.event.Listener;

public final class GeneralTracker implements Listener
{
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        String message = null;
        switch (event.getCause().getCause()) {
            case SUFFOCATION:
                message = "suffocated";
                break;
            case DROWNING:
                message = "drowned";
                break;
            case STARVATION:
                message = "starved to death";
                break;
            case LIGHTNING:
                message = "was struck by lightning";
                break;
            case POISON:
                message = "was poisoned";
                break;
            case WITHER:
                message = "withered away";
                break;
            case CONTACT:
                message = "was pricked to death";
                break;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                message = "was blown to smithereens";
                break;
            default:
                return;
        }
        final List<Damage> record = TeamsDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (final Damage damage : record) {
            if (!(damage instanceof GeneralDamage)) {
                if (damage instanceof GeneralDamageByPlayer) {
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
            event.setTrackerDamage(new GeneralDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager(), message));
        }
        else {
            event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), message));
        }
    }

    public static class GeneralDamage extends Damage
    {
        private final String message;

        public GeneralDamage(final UUID damaged, final double damage, final String message) {
            super(damaged, damage);
            this.message = message;
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + " " + ChatColor.YELLOW + this.message + ".";
        }
    }

    public static class GeneralDamageByPlayer extends PlayerDamage
    {
        private final String message;

        public GeneralDamageByPlayer(final UUID damaged, final double damage, final UUID damager, final String message) {
            super(damaged, damage, damager);
            this.message = message;
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + " " + ChatColor.YELLOW + this.message + " while fighting " + Damage.wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }
    }
}
