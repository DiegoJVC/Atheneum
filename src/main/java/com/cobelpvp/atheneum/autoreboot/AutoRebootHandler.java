package com.cobelpvp.atheneum.autoreboot;

import java.util.concurrent.TimeUnit;

import com.cobelpvp.atheneum.command.TeamsCommandHandler;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.autoreboot.tasks.ServerRebootTask;

import com.google.common.collect.ImmutableList;
import com.google.common.base.Preconditions;

import java.util.List;

public class AutoRebootHandler {

    private static List<Integer> rebootTimes;
    private static boolean initiated = false;
    private static ServerRebootTask serverRebootTask = null;

    private AutoRebootHandler() {
    }

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        TeamsCommandHandler.registerPackage(Atheneum.getInstance(), "com.cobelpvp.atheneum.autoreboot.command");
        rebootTimes = ImmutableList.copyOf(Atheneum.getInstance().getConfig().getIntegerList("AutoRebootTimes"));
    }

    @Deprecated
    public static void rebootServer(int seconds) {
        rebootServer(seconds, TimeUnit.SECONDS);
    }

    public static void rebootServer(int timeUnitAmount, TimeUnit timeUnit) {
        if (serverRebootTask != null) {
            throw new IllegalStateException("Reboot already in progress");
        }
        (serverRebootTask = new ServerRebootTask(timeUnitAmount, timeUnit)).runTaskTimer(Atheneum.getInstance(), 20L, 20L);
    }

    public static boolean isRebooting() {
        return serverRebootTask != null;
    }

    public static int getRebootSecondsRemaining() {
        if (serverRebootTask == null) {
            return -1;
        }
        return serverRebootTask.getSecondsRemaining();
    }

    public static void cancelReboot() {
        if (serverRebootTask != null) {
            serverRebootTask.cancel();
            serverRebootTask = null;
        }
    }

    public static List<Integer> getRebootTimes() {
        return rebootTimes;
    }

    public static boolean isInitiated() {
        return initiated;
    }
}
