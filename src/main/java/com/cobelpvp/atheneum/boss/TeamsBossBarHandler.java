package com.cobelpvp.atheneum.boss;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.util.EntityUtils;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.beans.ConstructorProperties;
import java.lang.reflect.Field;
import java.util.*;

public class TeamsBossBarHandler {

    private static final Map<UUID, BarData> displaying = new HashMap<UUID, BarData>();
    private static final Map<UUID, Integer> lastUpdatedPosition = new HashMap<UUID, Integer>();
    private static boolean initiated = false;
    private static Field spawnPacketAField = null;
    private static Field spawnPacketBField = null;
    private static Field spawnPacketCField = null;
    private static Field spawnPacketDField = null;
    private static Field spawnPacketEField = null;
    private static Field spawnPacketLField = null;
    private static Field metadataPacketAField = null;
    private static Field metadataPacketBField = null;
    private static TObjectIntHashMap classToIdMap = null;

    static {
        try {
            (spawnPacketAField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a")).setAccessible(true);
            (spawnPacketBField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("b")).setAccessible(true);
            (spawnPacketCField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c")).setAccessible(true);
            (spawnPacketDField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("d")).setAccessible(true);
            (spawnPacketEField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("e")).setAccessible(true);
            (spawnPacketLField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l")).setAccessible(true);
            (metadataPacketAField = PacketPlayOutEntityMetadata.class.getDeclaredField("a")).setAccessible(true);
            (metadataPacketBField = PacketPlayOutEntityMetadata.class.getDeclaredField("b")).setAccessible(true);
            Field dataWatcherClassToIdField = DataWatcher.class.getDeclaredField("classToId");
            dataWatcherClassToIdField.setAccessible(true);
            classToIdMap = (TObjectIntHashMap) dataWatcherClassToIdField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        Bukkit.getScheduler().runTaskTimer(Atheneum.getInstance(), () -> {
            Iterator var0 = displaying.keySet().iterator();

            while (var0.hasNext()) {
                UUID uuid = (UUID) var0.next();
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return;
                }

                int updateTicks = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() != 47 ? 60 : 3;
                if (lastUpdatedPosition.containsKey(player.getUniqueId()) && MinecraftServer.currentTick - lastUpdatedPosition.get(player.getUniqueId()) < updateTicks) {
                    return;
                }

                updatePosition(player);
                lastUpdatedPosition.put(player.getUniqueId(), MinecraftServer.currentTick);
            }

        }, 1L, 1L);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(final PlayerQuitEvent event) {
                TeamsBossBarHandler.removeBossBar(event.getPlayer());
            }

            @EventHandler
            public void onPlayerTeleport(final PlayerTeleportEvent event) {
                Player player = event.getPlayer();
                if (!displaying.containsKey(player.getUniqueId())) {
                    return;
                }
                BarData data = displaying.get(player.getUniqueId());
                String message = data.message;
                float health = data.health;
                TeamsBossBarHandler.removeBossBar(player);
                TeamsBossBarHandler.setBossBar(player, message, health);
            }
        }, Atheneum.getInstance());
    }

    public static void setBossBar(Player player, String message, float health) {
        try {
            if (message == null) {
                removeBossBar(player);
                return;
            }
            Preconditions.checkArgument(health >= 0.0f && health <= 1.0f, "Health must be between 0 and 1");
            if (message.length() > 64) {
                message = message.substring(0, 64);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
            if (!TeamsBossBarHandler.displaying.containsKey(player.getUniqueId())) {
                sendSpawnPacket(player, message, health);
            } else {
                sendUpdatePacket(player, message, health);
            }
            displaying.get(player.getUniqueId()).message = message;
            displaying.get(player.getUniqueId()).health = health;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeBossBar(Player player) {
        if (!displaying.containsKey(player.getUniqueId())) {
            return;
        }
        int entityId = displaying.get(player.getUniqueId()).entityId;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
        displaying.remove(player.getUniqueId());
        lastUpdatedPosition.remove(player.getUniqueId());
    }

    private static void sendSpawnPacket(Player bukkitPlayer, String message, float health) throws Exception {
        EntityPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        displaying.put(bukkitPlayer.getUniqueId(), new BarData(EntityUtils.getFakeEntityId(), message, health));
        BarData stored = TeamsBossBarHandler.displaying.get(bukkitPlayer.getUniqueId());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        spawnPacketAField.set(packet, stored.entityId);
        DataWatcher watcher = new DataWatcher((Entity) null);
        if (version != 47) {
            spawnPacketBField.set(packet, (byte) EntityType.ENDER_DRAGON.getTypeId());
            watcher.a(6, health * 200.0f);
            spawnPacketCField.set(packet, (int) (player.locX * 32.0));
            spawnPacketDField.set(packet, -6400);
            spawnPacketEField.set(packet, (int) (player.locZ * 32.0));
        } else {
            spawnPacketBField.set(packet, (byte) EntityType.WITHER.getTypeId());
            watcher.a(6, health * 300.0f);
            watcher.a(20, 880);
            double pitch = Math.toRadians(player.pitch);
            double yaw = Math.toRadians(player.yaw);
            spawnPacketCField.set(packet, (int) ((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0));
            spawnPacketDField.set(packet, (int) ((player.locY - Math.sin(pitch) * 32.0) * 32.0));
            spawnPacketEField.set(packet, (int) ((player.locZ + Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0));
        }
        watcher.a((version != 47) ? 10 : 2, message);
        spawnPacketLField.set(packet, watcher);
        player.playerConnection.sendPacket(packet);
    }

    private static void sendUpdatePacket(Player bukkitPlayer, String message, float health) throws IllegalAccessException {
        EntityPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        BarData stored = TeamsBossBarHandler.displaying.get(bukkitPlayer.getUniqueId());
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        metadataPacketAField.set(packet, stored.entityId);
        List<WatchableObject> objects = new ArrayList<WatchableObject>();
        if (health != stored.health) {
            if (version != 47) {
                objects.add(createWatchableObject(6, health * 200.0f));
            } else {
                objects.add(createWatchableObject(6, health * 300.0f));
            }
        }
        if (!message.equals(stored.message)) {
            objects.add(createWatchableObject((version != 47) ? 10 : 2, message));
        }
        metadataPacketBField.set(packet, objects);
        player.playerConnection.sendPacket(packet);
    }

    private static WatchableObject createWatchableObject(int id, Object object) {
        return new WatchableObject(TeamsBossBarHandler.classToIdMap.get(object.getClass()), id, object);
    }

    private static void updatePosition(final Player bukkitPlayer) {
        if (!displaying.containsKey(bukkitPlayer.getUniqueId())) {
            return;
        }
        EntityPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        int x;
        int y;
        int z;
        if (version != 47) {
            x = (int) (player.locX * 32.0);
            y = -6400;
            z = (int) (player.locZ * 32.0);
        } else {
            double pitch = Math.toRadians(player.pitch);
            double yaw = Math.toRadians(player.yaw);
            x = (int) ((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0);
            y = (int) ((player.locY - Math.sin(pitch) * 32.0) * 32.0);
            z = (int) ((player.locZ + Math.cos(yaw) * Math.cos(pitch) * 32.0) * 32.0);
        }
        player.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(TeamsBossBarHandler.displaying.get(bukkitPlayer.getUniqueId()).entityId, x, y, z, (byte) 0, (byte) 0));
    }

    private static class BarData {

        private final int entityId;
        private String message;
        private float health;

        @ConstructorProperties({"entityId", "message", "health"})
        public BarData(int entityId, String message, float health) {
            this.entityId = entityId;
            this.message = message;
            this.health = health;
        }
    }
}
