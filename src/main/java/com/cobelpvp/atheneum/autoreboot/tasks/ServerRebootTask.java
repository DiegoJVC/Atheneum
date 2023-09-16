package com.cobelpvp.atheneum.autoreboot.tasks;

import com.cobelpvp.atheneum.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerRebootTask extends BukkitRunnable {

    private int secondsRemaining;
    private boolean wasWhitelisted;

    public ServerRebootTask(int timeUnitAmount, TimeUnit timeUnit) {
        this.secondsRemaining = (int)timeUnit.toSeconds(timeUnitAmount);
        this.wasWhitelisted = Bukkit.getServer().hasWhitelist();
    }

    public void run() {
        if (this.secondsRemaining == 300) {
            Bukkit.getServer().setWhitelist(true);
        }
        else if (this.secondsRemaining == 0) {
            Bukkit.getServer().setWhitelist(this.wasWhitelisted);
            Bukkit.getServer().shutdown();
        }
        switch (this.secondsRemaining) {
            case 3:
            case 2:
            case 1:
            case 4:
            case 5:
            case 10:
            case 15:
            case 30:
            case 60:
            case 120:
            case 180:
            case 240:
            case 300:
                Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 10) + "[" + ChatColor.DARK_RED + "Auto Reboot" + "]" + ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + net.minecraft.util.org.apache.commons.lang3.StringUtils.repeat("-", 10));
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Server will be reboot in " + TimeUtils.formatIntoDetailedString(this.secondsRemaining));
                break;
        }
        --this.secondsRemaining;
    }

    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        Bukkit.setWhitelist(this.wasWhitelisted);
    }

    public int getSecondsRemaining() {
        return this.secondsRemaining;
    }
}
