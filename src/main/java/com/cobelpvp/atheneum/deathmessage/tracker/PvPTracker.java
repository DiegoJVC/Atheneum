package com.cobelpvp.atheneum.deathmessage.tracker;

import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import com.cobelpvp.atheneum.deathmessage.damage.PlayerDamage;
import com.cobelpvp.atheneum.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.util.UUID;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.Listener;

public final class PvPTracker implements Listener
{
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(final CustomPlayerDamageEvent event) {
        if (!(event.getCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        final EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent)event.getCause();
        final Entity damager = damageByEntityEvent.getDamager();
        if (damager instanceof Player) {
            final Player damaged = event.getPlayer();
            event.setTrackerDamage(new PvPDamage(damaged.getUniqueId(), event.getDamage(), (Player)damager));
        }
    }

    public static class PvPDamage extends PlayerDamage
    {
        private final String itemString;

        public PvPDamage(final UUID damaged, final double damage, final Player damager) {
            super(damaged, damage, damager.getUniqueId());
            final ItemStack hand = damager.getItemInHand();
            if (hand.getType() == Material.AIR) {
                this.itemString = "their fists";
            }
            else if (hand.getItemMeta().hasDisplayName()) {
                this.itemString = ChatColor.stripColor(hand.getItemMeta().getDisplayName());
            }
            else {
                this.itemString = WordUtils.capitalizeFully(hand.getType().name().replace('_', ' '));
            }
        }

        @Override
        public String getDeathMessage(final UUID getFor) {
            return Damage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was slain by " + Damage.wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + (TeamsDeathMessageHandler.getConfiguration().hideWeapons() ? "" : (" using " + ChatColor.RED + this.itemString.trim())) + ChatColor.YELLOW + ".";
        }
    }
}
