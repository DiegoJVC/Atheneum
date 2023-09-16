package com.cobelpvp.atheneum.tab;

import com.cobelpvp.atheneum.Atheneum;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;
import java.util.UUID;

public class TabAdapter extends PacketAdapter {
    private static Field playerField;
    private static Field namedEntitySpawnField;

    static {
        try {
            (TabAdapter.playerField = PacketPlayOutPlayerInfo.class.getDeclaredField("player")).setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            (TabAdapter.namedEntitySpawnField = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b")).setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TabAdapter() {
        super(Atheneum.getInstance(), PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    }

    public void onPacketSending(PacketEvent event) {
        if (TeamsTabHandler.getLayoutProvider() != null && this.shouldForbid(event.getPlayer())) {
            if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                PacketContainer packetContainer = event.getPacket();
                String name = packetContainer.getStrings().read(0);
                boolean isOurs = packetContainer.getStrings().read(0).startsWith("$");
                int action = packetContainer.getIntegers().read(1);
                if (!isOurs && !SpigotConfig.onlyCustomTab) {
                    if (action != 4 && this.shouldCancel(event.getPlayer(), event.getPacket())) {
                        event.setCancelled(true);
                    }
                } else {
                    packetContainer.getStrings().write(0, name.replace("$", ""));
                }
            } else if (event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN && TabUtils.is18(event.getPlayer()) && !SpigotConfig.onlyCustomTab && Bukkit.getPluginManager().getPlugin("UHC") == null) {
                PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn) event.getPacket().getHandle();

                GameProfile gameProfile;
                try {
                    gameProfile = (GameProfile) namedEntitySpawnField.get(packet);
                } catch (Exception var6) {
                    var6.printStackTrace();
                    return;
                }

                Bukkit.getScheduler().runTask(Atheneum.getInstance(), () -> {
                    Player bukkitPlayer = Bukkit.getPlayer(gameProfile.getId());
                    if (bukkitPlayer != null) {
                        ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) bukkitPlayer).getHandle()));
                    }
                });
            }

        }
    }

    private boolean shouldCancel(final Player player, final PacketContainer packetContainer) {
        if (!TabUtils.is18(player)) {
            return true;
        }
        final PacketPlayOutPlayerInfo playerInfoPacket = (PacketPlayOutPlayerInfo) packetContainer.getHandle();
        final EntityPlayer recipient = ((CraftPlayer) player).getHandle();
        UUID tabPacketPlayer;
        try {
            tabPacketPlayer = ((GameProfile) TabAdapter.playerField.get(playerInfoPacket)).getId();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        final Player bukkitPlayer = Bukkit.getPlayer(tabPacketPlayer);
        if (bukkitPlayer == null) {
            return true;
        }
        final EntityTrackerEntry trackerEntry = (EntityTrackerEntry) ((WorldServer) ((CraftPlayer) bukkitPlayer).getHandle().getWorld()).getTracker().trackedEntities.get(bukkitPlayer.getEntityId());
        return trackerEntry == null || !trackerEntry.trackedPlayers.contains(recipient);
    }

    private boolean shouldForbid(final Player player) {
        final String playerName = player.getName();
        final TeamsTab playerTab = TeamsTabHandler.getTabs().get(playerName);
        return playerTab != null && playerTab.isInitiated();
    }
}
