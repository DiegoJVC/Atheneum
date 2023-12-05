package com.cobelpvp.atheneum.hologram.construct;

import java.util.Collection;
import java.util.List;

import com.cobelpvp.atheneum.hologram.type.SerializedHologram;
import org.bukkit.Location;

public interface Hologram {
    int id();

    void send();

    void destroy();

    void delete();

    void move(Location var1);

    void setLine(int var1, String var2);

    void setLines(Collection<String> var1);

    void addLines(String... var1);

    void removeLine(int var1);

    List<String> getLines();

    Location getLocation();

    SerializedHologram toSerializedHologram();
}
