package com.cobelpvp.atheneum.deathmessage.listener;

import com.cobelpvp.atheneum.deathmessage.DeathMessageConfiguration;
import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import com.cobelpvp.atheneum.deathmessage.damage.PlayerDamage;
import com.cobelpvp.atheneum.deathmessage.damage.UnknownDamage;
import com.cobelpvp.atheneum.Atheneum;
import net.minecraft.server.v1_7_R4.EntityHuman;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.List;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.Listener;

public final class DeathListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathEarly(final PlayerDeathEvent event) {
        final List<Damage> record = TeamsDeathMessageHandler.getDamage(event.getEntity());
        if (!record.isEmpty()) {
            final Damage deathCause = record.get(record.size() - 1);
            if (deathCause instanceof PlayerDamage && deathCause.getTimeAgoMillis() < TimeUnit.MINUTES.toMillis(1L)) {
                final UUID killerUuid = ((PlayerDamage)deathCause).getDamager();
                final Player killerPlayer = Atheneum.getInstance().getServer().getPlayer(killerUuid);
                if (killerPlayer != null) {
                    ((CraftPlayer)event.getEntity()).getHandle().killer = (EntityHuman)((CraftPlayer)killerPlayer).getHandle();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathLate(final PlayerDeathEvent event) {
        final List<Damage> record = TeamsDeathMessageHandler.getDamage(event.getEntity());
        Damage deathCause;
        if (!record.isEmpty()) {
            deathCause = record.get(record.size() - 1);
        }
        else {
            deathCause = new UnknownDamage(event.getEntity().getUniqueId(), 1.0);
        }
        TeamsDeathMessageHandler.clearDamage(event.getEntity());
        event.setDeathMessage((String)null);
        final DeathMessageConfiguration configuration = TeamsDeathMessageHandler.getConfiguration();
        final UUID diedUuid = event.getEntity().getUniqueId();
        final UUID killerUuid = (event.getEntity().getKiller() == null) ? null : event.getEntity().getKiller().getUniqueId();
        for (final Player player : Atheneum.getInstance().getServer().getOnlinePlayers()) {
            final boolean showDeathMessage = configuration.shouldShowDeathMessage(player.getUniqueId(), diedUuid, killerUuid);
            if (showDeathMessage) {
                final String deathMessage = deathCause.getDeathMessage(player.getUniqueId());
                player.sendMessage(deathMessage);
            }
        }
    }
}
