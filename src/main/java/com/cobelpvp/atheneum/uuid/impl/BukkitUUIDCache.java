package com.cobelpvp.atheneum.uuid.impl;

import com.cobelpvp.atheneum.uuid.UUIDCache;
import com.cobelpvp.atheneum.Atheneum;

import java.util.UUID;

public final class BukkitUUIDCache implements UUIDCache {
    @Override
    public UUID uuid(final String name) {
        return Atheneum.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(final UUID uuid) {
        return Atheneum.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public void ensure(final UUID uuid) {
    }

    @Override
    public void update(final UUID uuid, final String name) {
    }
}
