package com.cobelpvp.atheneum.autoreboot.command;

import com.cobelpvp.atheneum.autoreboot.AutoRebootHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.TimeUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoRebootCommand {

    @Command(names = {"reboot"}, permission = "op")
    public static void reboot(CommandSender sender, @Param(name = "time") String unparsedTime) {
        try {
            int time = TimeUtils.parseTime(unparsedTime.toLowerCase());
            AutoRebootHandler.rebootServer(time, TimeUnit.SECONDS);
            sender.sendMessage(ChatColor.GOLD + "Auto Reboot have been started");
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
    }

    @Command(names = { "reboot cancel" }, permission = "op")
    public static void rebootCancel(CommandSender sender) {
        if (!AutoRebootHandler.isRebooting()) {
            sender.sendMessage(ChatColor.RED + "No reboot has been scheduled.");
            return;
        }
        AutoRebootHandler.cancelReboot();

        Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 10) + ChatColor.GOLD + "[" + ChatColor.DARK_RED + "Auto Reboot" + "]" + ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 10));
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Server reboot have been cancelled by " + (sender instanceof Player ? sender.getName() : ChatColor.GREEN + "SYSTEM"));
    }
}
