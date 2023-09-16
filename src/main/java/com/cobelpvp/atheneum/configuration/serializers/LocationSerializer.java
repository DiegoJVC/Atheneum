package com.cobelpvp.atheneum.configuration.serializers;

import com.cobelpvp.atheneum.configuration.AbstractSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer extends AbstractSerializer<Location>
{
    @Override
    public String toString(final Location data) {
        return data.getWorld().getName() + "|" + data.getBlockX() + "|" + data.getBlockY() + "|" + data.getBlockZ();
    }

    @Override
    public Location fromString(final String data) {
        final String[] parts = data.split("\\|");
        return new Location(Bukkit.getWorld(parts[0]), (double)Integer.valueOf(parts[1]), (double)Integer.valueOf(parts[2]), (double)Integer.valueOf(parts[3]));
    }
}
