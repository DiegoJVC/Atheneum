package com.cobelpvp.atheneum.deathmessage.tracker;

import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import com.cobelpvp.atheneum.deathmessage.damage.MobDamage;
import com.cobelpvp.atheneum.deathmessage.damage.PlayerDamage;
import com.cobelpvp.atheneum.deathmessage.event.CustomPlayerDamageEvent;
import com.cobelpvp.atheneum.util.EntityUtils;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.ChatColor;
import java.util.UUID;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.Listener;

public final class ArrowTracker implements Listener
{
    @EventHandler
    public void onEntityShootBow(final EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getProjectile().setMetadata("ShotFromDistance", (MetadataValue)new FixedMetadataValue((Plugin) Atheneum.getInstance(), (Object)event.getProjectile().getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        if (!(event.getCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        final EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent)event.getCause();
        if (!(damageByEntityEvent.getDamager() instanceof Arrow)) {
            return;
        }
        final Arrow arrow = (Arrow)damageByEntityEvent.getDamager();
        if (arrow.getShooter() instanceof Player) {
            final Player shooter = (Player)arrow.getShooter();
            for (final MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
                final Location shotFrom = (Location)value.value();
                final double distance = shotFrom.distance(event.getPlayer().getLocation());
                event.setTrackerDamage(new ArrowDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), shooter.getUniqueId(), distance));
            }
        }
        else if (arrow.getShooter() != null) {
            if (arrow.getShooter() instanceof Entity) {
                event.setTrackerDamage(new ArrowDamageByMob(event.getPlayer().getUniqueId(), event.getDamage(), (Entity)arrow.getShooter()));
            }
        }
        else {
            event.setTrackerDamage(new ArrowDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class ArrowDamage extends Damage
    {
        public ArrowDamage(final UUID damaged, final double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was shot.";
        }
    }

    public static class ArrowDamageByPlayer extends PlayerDamage
    {
        private final double distance;

        public ArrowDamageByPlayer(final UUID damaged, final double damage, final UUID damager, final double distance) {
            super(damaged, damage, damager);
            this.distance = distance;
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was shot by " + Damage.wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + " from " + ChatColor.BLUE + (int)this.distance + " blocks" + ChatColor.YELLOW + ".";
        }
    }

    public static class ArrowDamageByMob extends MobDamage
    {
        public ArrowDamageByMob(final UUID damaged, final double damage, final Entity damager) {
            super(damaged, damage, damager.getType());
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was shot by a " + ChatColor.RED + EntityUtils.getName(this.getMobType()) + ChatColor.YELLOW + ".";
        }
    }
}
