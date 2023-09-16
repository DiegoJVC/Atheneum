package com.cobelpvp.atheneum.command;

import org.bukkit.ChatColor;

public class CommandConfiguration {
    private String noPermissionMessage;

    public String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }

    public CommandConfiguration setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = ChatColor.translateAlternateColorCodes('&', noPermissionMessage);
        return this;
    }
}
