package com.cobelpvp.atheneum.hologram.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import com.cobelpvp.atheneum.hologram.type.BaseHologram;
import org.bukkit.Location;

public class HologramBuilder {
    protected List<String> lines;
    private Location location;
    private Collection<UUID> viewers;

    public HologramBuilder(Collection<UUID> viewers) {
        this.viewers = viewers;
        this.lines = new ArrayList();
    }

    public HologramBuilder addLines(Iterable<String> lines) {
        Iterator var2 = lines.iterator();

        while(var2.hasNext()) {
            String line = (String)var2.next();
            this.lines.add(line);
        }

        return this;
    }

    public HologramBuilder addLines(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    public HologramBuilder at(Location location) {
        this.location = location;
        return this;
    }

    public UpdatingHologramBuilder updates() {
        return new UpdatingHologramBuilder(this);
    }

    public Hologram build() {
        return this.build(Atheneum.getInstance().getHologramHandler().createId());
    }

    public Hologram build(int id) {
        return new BaseHologram(this, id);
    }

    public List<String> getLines() {
        return this.lines;
    }

    public Location getLocation() {
        return this.location;
    }

    public Collection<UUID> getViewers() {
        return this.viewers;
    }
}
