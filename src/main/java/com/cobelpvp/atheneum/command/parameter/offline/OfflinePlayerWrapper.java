package com.cobelpvp.atheneum.command.parameter.offline;

import com.cobelpvp.atheneum.util.Callback;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.Atheneum;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class OfflinePlayerWrapper {
    private String source;
    private UUID uniqueId;
    private String name;

    public OfflinePlayerWrapper(final String source) {
        this.source = source;
    }

    public void loadAsync(final Callback<Player> callback) {
        new BukkitRunnable() {
            public void run() {
                final Player player = OfflinePlayerWrapper.this.loadSync();
                new BukkitRunnable() {
                    public void run() {
                        callback.callback(player);
                    }
                }.runTask(Atheneum.getInstance());
            }
        }.runTaskAsynchronously(Atheneum.getInstance());
    }

    public Player loadSync() {
        if ((this.source.charAt(0) == '\"' || this.source.charAt(0) == '\'') && (this.source.charAt(this.source.length() - 1) == '\"' || this.source.charAt(this.source.length() - 1) == '\'')) {
            this.source = this.source.replace("'", "").replace("\"", "");
            this.uniqueId = UUIDUtils.uuid(this.source);
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }
            this.name = UUIDUtils.name(this.uniqueId);
            if (Bukkit.getPlayer(this.uniqueId) != null) {
                return Bukkit.getPlayer(this.uniqueId);
            }
            if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }
            final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
            final Player player = entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
            }
            return player;
        } else {
            if (Bukkit.getPlayer(this.source) != null) {
                return Bukkit.getPlayer(this.source);
            }
            this.uniqueId = UUIDUtils.uuid(this.source);
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }
            this.name = UUIDUtils.name(this.uniqueId);
            if (Bukkit.getPlayer(this.uniqueId) != null) {
                return Bukkit.getPlayer(this.uniqueId);
            }
            if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }
            final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
            final Player player = entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
            }
            return player;
        }
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }
}