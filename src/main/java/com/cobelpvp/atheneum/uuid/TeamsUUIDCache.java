package com.cobelpvp.atheneum.uuid;

import com.google.common.base.Preconditions;
import com.cobelpvp.atheneum.Atheneum;

import java.util.UUID;

public final class TeamsUUIDCache {
    private static UUIDCache impl;
    private static boolean initiated;

    static {
        TeamsUUIDCache.impl = null;
        TeamsUUIDCache.initiated = false;
    }

    private TeamsUUIDCache() {
    }

    public static void init() {
        Preconditions.checkState(!TeamsUUIDCache.initiated);
        TeamsUUIDCache.initiated = true;
        try {
            TeamsUUIDCache.impl = (UUIDCache) Class.forName(Atheneum.getInstance().getConfig().getString("UUIDCache.Backend", "com.cobelpvp.atheneum.uuid.impl.RedisUUIDCache")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Atheneum.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), Atheneum.getInstance());
    }

    public static UUID uuid(final String name) {
        return TeamsUUIDCache.impl.uuid(name);
    }

    public static String name(final UUID uuid) {
        return TeamsUUIDCache.impl.name(uuid);
    }

    public static void ensure(final UUID uuid) {
        TeamsUUIDCache.impl.ensure(uuid);
    }

    public static void update(final UUID uuid, final String name) {
        TeamsUUIDCache.impl.update(uuid, name);
    }

    public static UUIDCache getImpl() {
        return TeamsUUIDCache.impl;
    }
}
