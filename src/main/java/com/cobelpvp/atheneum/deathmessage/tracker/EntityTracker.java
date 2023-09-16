package com.cobelpvp.atheneum.deathmessage.tracker;

import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import com.cobelpvp.atheneum.deathmessage.damage.MobDamage;
import com.cobelpvp.atheneum.deathmessage.event.CustomPlayerDamageEvent;
import com.cobelpvp.atheneum.util.EntityUtils;
import org.bukkit.ChatColor;
import java.util.UUID;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.Listener;

public final class EntityTracker implements Listener
{
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        if (!(event.getCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        final EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent)event.getCause();
        final Entity damager = damageByEntityEvent.getDamager();
        if (damager instanceof LivingEntity && !(damager instanceof Player)) {
            event.setTrackerDamage(new EntityDamage(event.getPlayer().getUniqueId(), event.getDamage(), damager));
        }
    }

    public static class EntityDamage extends MobDamage
    {
        public EntityDamage(final UUID damaged, final double damage, final Entity entity) {
            super(damaged, damage, entity.getType());
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was slain by a " + ChatColor.RED + EntityUtils.getName(this.getMobType()) + ChatColor.YELLOW + ".";
        }
    }
}
