package com.cobelpvp.atheneum.hologram.builder;

import com.cobelpvp.atheneum.hologram.construct.Hologram;
import com.cobelpvp.atheneum.hologram.type.UpdatingHologram;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class UpdatingHologramBuilder extends HologramBuilder {
    private long interval;
    private Consumer<Hologram> updateFunction;

    public UpdatingHologramBuilder(HologramBuilder hologramBuilder) {
        super(hologramBuilder.getViewers());
        this.lines = hologramBuilder.getLines();
        this.at(hologramBuilder.getLocation());
    }

    public UpdatingHologramBuilder interval(long time, TimeUnit unit) {
        this.interval = unit.toSeconds(time);
        return this;
    }

    public UpdatingHologramBuilder onUpdate(Consumer<Hologram> onUpdate) {
        this.updateFunction = onUpdate;
        return this;
    }

    public Hologram build() {
        return new UpdatingHologram(this);
    }

    public long getInterval() {
        return this.interval;
    }

    public Consumer<Hologram> getUpdateFunction() {
        return this.updateFunction;
    }
}
