package com.cobelpvp.atheneum.util.countdown;

import com.cobelpvp.atheneum.util.TimeUtils;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Countdown extends BukkitRunnable {
    private final String broadcastMessage;
    private final int[] broadcastAt;
    private final Runnable tickHandler;
    private final Runnable broadcastHandler;
    private final Runnable finishHandler;
    private final Predicate<Player> messageFilter;
    private int seconds;
    private boolean first;

    Countdown(final int seconds, final String broadcastMessage, final Runnable tickHandler, final Runnable broadcastHandler, final Runnable finishHandler, final Predicate<Player> messageFilter, final int... broadcastAt) {
        this.first = true;
        this.seconds = seconds;
        this.broadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);
        this.broadcastAt = broadcastAt;
        this.tickHandler = tickHandler;
        this.broadcastHandler = broadcastHandler;
        this.finishHandler = finishHandler;
        this.messageFilter = messageFilter;
        this.runTaskTimer(Atheneum.getInstance(), 0L, 20L);
    }

    public static CountdownBuilder of(final int amount, final TimeUnit unit) {
        return new CountdownBuilder((int) unit.toSeconds(amount));
    }

    public final void run() {
        if (!this.first) {
            --this.seconds;
        } else {
            this.first = false;
        }
        for (final int index : this.broadcastAt) {
            if (this.seconds == index) {
                final String message = this.broadcastMessage.replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds));
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (this.messageFilter == null || this.messageFilter.test(player)) {
                        player.sendMessage(message);
                    }
                }
                if (this.broadcastHandler != null) {
                    this.broadcastHandler.run();
                }
            }
        }
        if (this.seconds == 0) {
            if (this.finishHandler != null) {
                this.finishHandler.run();
            }
            this.cancel();
        } else if (this.tickHandler != null) {
            this.tickHandler.run();
        }
    }

    public int getSecondsRemaining() {
        return this.seconds;
    }
}
