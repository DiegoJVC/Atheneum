package com.cobelpvp.atheneum.tab;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabThread extends Thread {
    private final Plugin protocolLib;

    public TabThread() {
        this.protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");
        this.setName("qLib - Tab Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (Atheneum.getInstance().isEnabled() && this.protocolLib != null && this.protocolLib.isEnabled()) {
            for (final Player online : Atheneum.getInstance().getServer().getOnlinePlayers()) {
                try {
                    TeamsTabHandler.updatePlayer(online);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(250L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
}
