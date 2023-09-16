package com.cobelpvp.atheneum.uuid.impl;

import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.atheneum.uuid.UUIDCache;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.redis.RedisCommand;
import com.cobelpvp.atheneum.xpacket.TeamsXPacketHandler;
import com.cobelpvp.atheneum.xpacket.XPacket;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DistributedUUIDCache implements UUIDCache {
    private static Map<UUID, String> uuidToName;
    private static Map<String, UUID> nameToUuid;

    static {
        DistributedUUIDCache.uuidToName = new ConcurrentHashMap<UUID, String>();
        DistributedUUIDCache.nameToUuid = new ConcurrentHashMap<String, UUID>();
    }

    public DistributedUUIDCache() {
        Atheneum.getInstance().runBackboneRedisCommand(new RedisCommand<Object>() {
            @Override
            public Object execute(final Jedis redis) {
                final Map<String, String> cache = redis.hgetAll("UUIDCache");
                for (final Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                    final UUID uuid = UUID.fromString(cacheEntry.getKey());
                    final String name = cacheEntry.getValue();
                    DistributedUUIDCache.uuidToName.put(uuid, name);
                    DistributedUUIDCache.nameToUuid.put(name.toLowerCase(), uuid);
                }
                return null;
            }
        });
    }

    @Override
    public UUID uuid(final String name) {
        return DistributedUUIDCache.nameToUuid.get(name.toLowerCase());
    }

    @Override
    public String name(final UUID uuid) {
        return DistributedUUIDCache.uuidToName.get(uuid);
    }

    @Override
    public void ensure(final UUID uuid) {
        if (String.valueOf(this.name(uuid)).equals("null")) {
            Atheneum.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
        }
    }

    @Override
    public void update(final UUID uuid, final String name) {
        this.update0(uuid, name, true);
    }

    private void update0(final UUID uuid, final String name, final boolean distributedToOthers) {
        DistributedUUIDCache.uuidToName.put(uuid, name);
        for (final Map.Entry<String, UUID> entry : new HashMap<String, UUID>(DistributedUUIDCache.nameToUuid).entrySet()) {
            if (entry.getValue().equals(uuid)) {
                DistributedUUIDCache.nameToUuid.remove(entry.getKey());
            }
        }
        DistributedUUIDCache.nameToUuid.put(name.toLowerCase(), uuid);
        if (distributedToOthers) {
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
            final DistributedUUIDCacheUpdatePacket packet = new DistributedUUIDCacheUpdatePacket(uuid, name);
            TeamsXPacketHandler.sendToAll(packet);
        }
    }

    public static class DistributedUUIDCacheUpdatePacket implements XPacket {
        private UUID uuid;
        private String name;

        public DistributedUUIDCacheUpdatePacket() {
        }

        @ConstructorProperties({"uuid", "name"})
        public DistributedUUIDCacheUpdatePacket(final UUID uuid, final String name) {
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public void onReceive() {
            if (TeamsUUIDCache.getImpl() instanceof DistributedUUIDCache) {
                ((DistributedUUIDCache) TeamsUUIDCache.getImpl()).update0(this.uuid, this.name, false);
            }
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }
}
