package com.cobelpvp.atheneum.util;

import org.bukkit.scheduler.BukkitRunnable;

public class TPSUtils extends BukkitRunnable {
    private static int TICK_COUNT;
    private static long[] TICKS;

    static {
        TPSUtils.TICK_COUNT = 0;
        TPSUtils.TICKS = new long[600];
    }

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(final int ticks) {
        if (TPSUtils.TICK_COUNT < ticks) {
            return 20.0;
        }
        final int target = (TPSUtils.TICK_COUNT - 1 - ticks) % TPSUtils.TICKS.length;
        final long elapsed = System.currentTimeMillis() - TPSUtils.TICKS[target];
        return ticks / (elapsed / 1000.0);
    }

    public void run() {
        TPSUtils.TICKS[TPSUtils.TICK_COUNT % TPSUtils.TICKS.length] = System.currentTimeMillis();
        ++TPSUtils.TICK_COUNT;
    }
}
