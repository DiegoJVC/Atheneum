package com.cobelpvp.atheneum.tab;

import com.cobelpvp.atheneum.Atheneum;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TabUtils {
    private static Map<String, GameProfile> cache;

    static {
        TabUtils.cache = new ConcurrentHashMap<String, GameProfile>();
    }

    public static boolean is18(final Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() > 20;
    }

    public static GameProfile getOrCreateProfile(final String name, final UUID id) {
        GameProfile player = TabUtils.cache.get(name);
        if (player == null) {
            player = new GameProfile(id, name);
            player.getProperties().putAll(TeamsTabHandler.getDefaultPropertyMap());
            TabUtils.cache.put(name, player);
        }
        return player;
    }

    public static GameProfile getOrCreateProfile(final String name) {
        return getOrCreateProfile(name, new UUID(Atheneum.RANDOM.nextLong(), Atheneum.RANDOM.nextLong()));
    }
}
