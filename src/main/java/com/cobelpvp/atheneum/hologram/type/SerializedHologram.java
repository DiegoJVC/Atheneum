package com.cobelpvp.atheneum.hologram.type;
import java.beans.ConstructorProperties;
import java.util.List;
import org.bukkit.Location;

public class SerializedHologram {
    private int id;
    private Location location;
    private List<String> lines;

    @ConstructorProperties({"id", "location", "lines"})
    public SerializedHologram(int id, Location location, List<String> lines) {
        this.id = id;
        this.location = location;
        this.lines = lines;
    }

    public int getId() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public List<String> getLines() {
        return this.lines;
    }
}
