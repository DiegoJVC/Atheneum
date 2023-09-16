package com.cobelpvp.atheneum.uuid.impl;

import com.cobelpvp.atheneum.uuid.UUIDCache;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.redis.RedisCommand;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisUUIDCache implements UUIDCache {
    private static Map<UUID, String> uuidToName;
    private static Map<String, UUID> nameToUuid;

    static {
        RedisUUIDCache.uuidToName = new ConcurrentHashMap<UUID, String>();
        RedisUUIDCache.nameToUuid = new ConcurrentHashMap<String, UUID>();
    }

    public RedisUUIDCache() {
        Atheneum.getInstance().runBackboneRedisCommand(new RedisCommand<Object>() {
            @Override
            public Object execute(final Jedis redis) {
                final Map<String, String> cache = redis.hgetAll("UUIDCache");
                for (final Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                    final UUID uuid = UUID.fromString(cacheEntry.getKey());
                    final String name = cacheEntry.getValue();
                    RedisUUIDCache.uuidToName.put(uuid, name);
                    RedisUUIDCache.nameToUuid.put(name.toLowerCase(), uuid);
                }
                return null;
            }
        });
    }

    @Override
    public UUID uuid(final String name) {
        return RedisUUIDCache.nameToUuid.get(name.toLowerCase());
    }

    @Override
    public String name(final UUID uuid) {
        return RedisUUIDCache.uuidToName.get(uuid);
    }

    @Override
    public void ensure(final UUID uuid) {
        if (String.valueOf(this.name(uuid)).equals("null")) {
            Atheneum.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
        }
    }

    @Override
    public void update(final UUID uuid, final String name) {
        RedisUUIDCache.uuidToName.put(uuid, name);
        for (final Map.Entry<String, UUID> entry : new HashMap<String, UUID>(RedisUUIDCache.nameToUuid).entrySet()) {
            if (entry.getValue().equals(uuid)) {
                RedisUUIDCache.nameToUuid.remove(entry.getKey());
            }
        }
        RedisUUIDCache.nameToUuid.put(name.toLowerCase(), uuid);
        new BukkitRunnable() {
            public void run() {
                Atheneum.getInstance().runBackboneRedisCommand(new RedisCommand<Object>() {
                    @Override
                    public Object execute(final Jedis redis) {
                        redis.hset("UUIDCache", uuid.toString(), name);
                        return null;
                    }
                });
            }
        }.runTaskAsynchronously(Atheneum.getInstance());
    }
}
