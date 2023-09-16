package com.cobelpvp.atheneum.combatlogger;

import com.cobelpvp.atheneum.deathmessage.DeathMessageConfiguration;
import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.UUID;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.EventHandler;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.Listener;

public class CombatLoggerListener implements Listener
{
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata("Libs-CombatLogger")) {
            CombatLogger logger = TeamsCombatLoggerHandler.getCombatLoggerMap().get(event.getEntity().getUniqueId());
            if (logger != null) {
                for (ItemStack item : logger.getArmor()) {
                    event.getDrops().add(item);
                }
                for (ItemStack item : logger.getInventory()) {
                    event.getDrops().add(item);
                }
                logger.getEventAdapter().onEntityDeath(logger, event);
                CombatLoggerConfiguration configuration = TeamsCombatLoggerHandler.getConfiguration();
                DeathMessageConfiguration dmConfig = TeamsDeathMessageHandler.getConfiguration();
                Player killer = event.getEntity().getKiller();
                if (configuration != null && dmConfig != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player player : Atheneum.getInstance().getServer().getOnlinePlayers()) {
                                String deathMessage = getCombatLoggerDeathMessage(logger.getPlayerUuid(), killer, player.getUniqueId());
                                boolean showDeathMessage = dmConfig.shouldShowDeathMessage(player.getUniqueId(), logger.getPlayerUuid(), (killer == null) ? null : killer.getUniqueId());
                                if (showDeathMessage) {
                                    player.sendMessage(deathMessage);
                                }
                            }
                        }
                    }.runTaskAsynchronously((Plugin) Atheneum.getInstance());
                }
                Player target = Atheneum.getInstance().getServer().getPlayer(logger.getPlayerUuid());
                if (target == null) {
                    MinecraftServer server = ((CraftServer) Atheneum.getInstance().getServer()).getServer();
                    EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(logger.getPlayerUuid(), logger.getPlayerName()), new PlayerInteractManager(server.getWorldServer(0)));
                    target = entity.getBukkitEntity();
                    if (target != null) {
                        target.loadData();
                    }
                }
                if (target != null) {
                    target.getInventory().clear();
                    target.getInventory().setArmorContents(null);
                    target.saveData();
                }
                TeamsCombatLoggerHandler.getCombatLoggerMap().remove(event.getEntity().getUniqueId());
                TeamsCombatLoggerHandler.getCombatLoggerMap().remove(logger.getPlayerUuid());
            }
        }
    }

    @EventHandler
    public void onEntityInteract(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata("Libs-CombatLogger")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (final Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata("Libs-CombatLogger") && !entity.isDead()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata("Libs-CombatLogger")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CombatLogger logger = TeamsCombatLoggerHandler.getCombatLoggerMap().get(event.getPlayer().getUniqueId());
        if (logger != null && logger.getSpawnedEntity() != null && logger.getSpawnedEntity().isValid() && !logger.getSpawnedEntity().isDead()) {
            final UUID entityId = logger.getSpawnedEntity().getUniqueId();
            logger.getSpawnedEntity().remove();
            TeamsCombatLoggerHandler.getCombatLoggerMap().remove(entityId);
            TeamsCombatLoggerHandler.getCombatLoggerMap().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata("Libs-CombatLogger")) {
            return;
        }
        CombatLogger logger = TeamsCombatLoggerHandler.getCombatLoggerMap().get(event.getEntity().getUniqueId());
        if (logger != null) {
            logger.getEventAdapter().onEntityDamageByEntity(logger, event);
        }
    }

    @EventHandler
    public void onEntityPressurePlate(EntityInteractEvent event) {
        boolean pressurePlate = event.getBlock().getType() == Material.STONE_PLATE || event.getBlock().getType() == Material.GOLD_PLATE || event.getBlock().getType() == Material.IRON_PLATE || event.getBlock().getType() == Material.WOOD_PLATE;
        if (pressurePlate && event.getEntity().hasMetadata("Libs-CombatLogger")) {
            event.setCancelled(true);
        }
    }

    private String getCombatLoggerDeathMessage(UUID player, Player killer, UUID getFor) {
        if (killer == null) {
            return this.wrapLogger(player, getFor) + ChatColor.YELLOW + " died.";
        }
        ItemStack hand = killer.getItemInHand();
        String itemString;
        if (hand.getType() == Material.AIR) {
            itemString = "their fists";
        }
        else if (hand.getItemMeta().hasDisplayName()) {
            itemString = ChatColor.stripColor(hand.getItemMeta().getDisplayName());
        }
        else {
            itemString = WordUtils.capitalizeFully(hand.getType().name().replace('_', ' '));
        }
        return this.wrapLogger(player, getFor) + ChatColor.YELLOW + " was slain by " + Damage.wrapName(killer.getUniqueId(), getFor) + ChatColor.YELLOW + " using " + ChatColor.RED + itemString.trim() + ChatColor.YELLOW + ".";
    }

    private String wrapLogger(UUID player, UUID wrapFor) {
        CombatLoggerConfiguration configuration = TeamsCombatLoggerHandler.getConfiguration();
        return configuration.formatPlayerName(player, wrapFor);
    }
}
