package com.cobelpvp.atheneum.hologram.type;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.hologram.builder.HologramBuilder;
import com.cobelpvp.atheneum.hologram.builder.UpdatingHologramBuilder;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public final class UpdatingHologram extends BaseHologram {
    private long interval;

    private Consumer<Hologram> updateFunction;

    private boolean showing = false;

    public UpdatingHologram(UpdatingHologramBuilder builder) {
        super((HologramBuilder)builder);
        this.interval = builder.getInterval();
        this.updateFunction = builder.getUpdateFunction();
    }

    public void send() {
        if (this.showing) {
            update();
        } else {
            super.send();
            this.showing = true;
            Atheneum.getInstance().getServer().getScheduler().runTaskTimerAsynchronously((Plugin)Atheneum.getInstance(), new BukkitRunnable() {
                public void run() {
                    if (UpdatingHologram.this.showing) {
                        UpdatingHologram.this.update();
                    } else {
                        cancel();
                    }
                }
            },  0L, this.interval * 20L);
        }
    }

    public void destroy() {
        super.destroy();
        this.showing = false;
    }

    public void update() {
        this.updateFunction.accept(this);
        super.update();
    }
}
