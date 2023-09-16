package com.cobelpvp.atheneum.util;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class BungeeUtils {
    private BungeeUtils() {
    }

    public static void send(final Player player, final String server) {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException ex) {
        }
        player.sendPluginMessage(Atheneum.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void sendAll(final String server) {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException ex) {
        }
        for (final Player player : Atheneum.getInstance().getServer().getOnlinePlayers()) {
            player.sendPluginMessage(Atheneum.getInstance(), "BungeeCord", b.toByteArray());
        }
    }
}
