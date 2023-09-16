package com.cobelpvp.atheneum.util;

import com.mongodb.BasicDBList;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;

import java.util.Collection;
import java.util.UUID;

public final class UUIDUtils {
    private UUIDUtils() {
    }

    public static String name(final UUID uuid) {
        final String name = TeamsUUIDCache.name(uuid);
        return (name == null) ? "null" : name;
    }

    public static UUID uuid(final String name) {
        return TeamsUUIDCache.uuid(name);
    }

    public static String formatPretty(final UUID uuid) {
        return name(uuid) + " [" + uuid + "]";
    }

    public static BasicDBList uuidsToStrings(final Collection<UUID> toConvert) {
        if (toConvert == null || toConvert.isEmpty()) {
            return new BasicDBList();
        }
        final BasicDBList dbList = new BasicDBList();
        for (final UUID uuid : toConvert) {
            dbList.add(uuid.toString());
        }
        return dbList;
    }
}
