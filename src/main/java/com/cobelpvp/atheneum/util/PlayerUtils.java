package com.cobelpvp.atheneum.util;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.protocol.InventoryAdapter;
import com.cobelpvp.atheneum.protocol.PingAdapter;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class PlayerUtils {
    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;
    private static Field SPAWN_PACKET_ID_FIELD;

    public static boolean is18(Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 20;
    }

    static {
        try {
            (PlayerUtils.STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a")).setAccessible(true);
            (PlayerUtils.STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b")).setAccessible(true);
            (PlayerUtils.SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a")).setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private PlayerUtils() {
    }

    public static void resetInventory(final Player player) {
        resetInventory(player, null);
    }

    public static void resetInventory(final Player player, final GameMode gameMode) {
        player.setHealth(player.getMaxHealth());
        player.setFallDistance(0.0f);
        player.setFoodLevel(20);
        player.setSaturation(10.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        if (!player.hasMetadata("modmode")) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }
        player.setFireTicks(0);
        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        if (gameMode != null && player.getGameMode() != gameMode) {
            player.setGameMode(gameMode);
        }
    }

    public static Player getDamageSource(final Entity damager) {
        Player playerDamager = null;
        if (damager instanceof Player) {
            playerDamager = (Player) damager;
        } else if (damager instanceof Projectile) {
            final Projectile projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                playerDamager = (Player) projectile.getShooter();
            }
        }
        return playerDamager;
    }

    public static boolean hasOpenInventory(final Player player) {
        return hasOwnInventoryOpen(player) || hasOtherInventoryOpen(player);
    }

    public static boolean hasOwnInventoryOpen(final Player player) {
        return InventoryAdapter.getCurrentlyOpen().contains(player.getUniqueId());
    }

    public static boolean hasOtherInventoryOpen(final Player player) {
        return ((CraftPlayer) player).getHandle().activeContainer.windowId != 0;
    }

    public static int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    public static boolean isLagging(final Player player) {
        return !PingAdapter.getLastReply().containsKey(player.getUniqueId()) || MinecraftServer.currentTick - PingAdapter.getLastReply().get(player.getUniqueId()) > 40;
    }

    public static void animateDeath(Player player) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            int radius = MinecraftServer.getServer().getPlayerList().d();
            Set<Player> sentTo = new HashSet();
            Iterator var6 = player.getNearbyEntities(radius, radius, radius).iterator();

            while (var6.hasNext()) {
                Entity entity = (Entity) var6.next();
                if (entity instanceof Player) {
                    Player watcher = (Player) entity;
                    if (!watcher.getUniqueId().equals(player.getUniqueId())) {
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
                        sentTo.add(watcher);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(Atheneum.getInstance(), () -> {
                Iterator var2 = sentTo.iterator();

                while (var2.hasNext()) {
                    Player watcher = (Player) var2.next();
                    ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }

            }, 40L);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public static void animateDeath(Player player, Player watcher) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
            Bukkit.getScheduler().runTaskLater(Atheneum.getInstance(), () -> {
                ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
            }, 40L);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
}
