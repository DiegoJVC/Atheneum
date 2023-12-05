package com.cobelpvp.atheneum.hologram.listener;

import java.util.Iterator;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import com.cobelpvp.atheneum.hologram.type.BaseHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class HologramListener implements Listener {
    public HologramListener() {
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Atheneum.getInstance().getServer().getScheduler().runTaskLater(Atheneum.getInstance(), () -> {
            Atheneum.getInstance().getHologramHandler().getCache().values().forEach((hologram) -> {
                BaseHologram baseHologram = (BaseHologram)hologram;
                if ((baseHologram.getViewers() == null || baseHologram.getViewers().contains(event.getPlayer().getUniqueId())) && baseHologram.getLocation().getWorld().equals(event.getPlayer().getWorld()) && hologram.getLocation().distance(event.getPlayer().getLocation()) <= 1600.0) {
                    baseHologram.show(event.getPlayer());
                }

            });
        }, 20L);
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
            Iterator var5 = Atheneum.getInstance().getHologramHandler().getCache().values().iterator();

            while(true) {
                while(true) {
                    Hologram hologram;
                    BaseHologram baseHologram;
                    do {
                        do {
                            if (!var5.hasNext()) {
                                return;
                            }

                            hologram = (Hologram)var5.next();
                            baseHologram = (BaseHologram)hologram;
                        } while(baseHologram.getViewers() != null && !baseHologram.getViewers().contains(event.getPlayer().getUniqueId()));
                    } while(!hologram.getLocation().getWorld().equals(event.getPlayer().getWorld()));

                    if (!baseHologram.getCurrentWatchers().contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0) {
                        baseHologram.show(player);
                    } else if (baseHologram.getCurrentWatchers().contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) > 1600.0) {
                        baseHologram.destroy0(player);
                    }
                }
            }
        }
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        Iterator var2 = Atheneum.getInstance().getHologramHandler().getCache().values().iterator();

        while(true) {
            BaseHologram baseHologram;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                Hologram hologram = (Hologram)var2.next();
                baseHologram = (BaseHologram)hologram;
            } while(baseHologram.getViewers() != null && !baseHologram.getViewers().contains(event.getPlayer().getUniqueId()));

            if (baseHologram.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                baseHologram.show(event.getPlayer());
            }
        }
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void onRespawn(PlayerRespawnEvent event) {
        Atheneum.getInstance().getServer().getScheduler().runTaskLater(Atheneum.getInstance(), () -> {
            Iterator var1 = Atheneum.getInstance().getHologramHandler().getCache().values().iterator();

            while(true) {
                BaseHologram baseHologram;
                do {
                    if (!var1.hasNext()) {
                        return;
                    }

                    Hologram hologram = (Hologram)var1.next();
                    baseHologram = (BaseHologram)hologram;
                    baseHologram.destroy0(event.getPlayer());
                } while(baseHologram.getViewers() != null && !baseHologram.getViewers().contains(event.getPlayer().getUniqueId()));

                if (baseHologram.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                    baseHologram.show(event.getPlayer());
                }
            }
        }, 10L);
    }
}
