package com.cobelpvp.atheneum.xpacket;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

final class XPacketPubSub extends JedisPubSub
{
    @Override
    public void onMessage(final String channel, final String message) {
        final int packetMessageSplit = message.indexOf("||");
        final String packetClassStr = message.substring(0, packetMessageSplit);
        final String messageJson = message.substring(packetMessageSplit + "||".length());
        Class<?> packetClass;
        try {
            packetClass = Class.forName(packetClassStr);
        }
        catch (ClassNotFoundException ignored) {
            return;
        }
        final XPacket packet = (XPacket) Atheneum.PLAIN_GSON.fromJson(messageJson, (Class)packetClass);
        if (Atheneum.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTask((Plugin) Atheneum.getInstance(), packet::onReceive);
        }
    }
}
