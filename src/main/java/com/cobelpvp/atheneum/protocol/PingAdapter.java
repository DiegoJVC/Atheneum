package com.cobelpvp.atheneum.protocol;

import java.beans.ConstructorProperties;
import com.google.common.collect.Maps;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.Iterator;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import java.util.UUID;
import java.util.Map;
import org.bukkit.event.Listener;
import com.comphenix.protocol.events.PacketAdapter;

public class PingAdapter extends PacketAdapter implements Listener
{
    private static final Map<UUID, PingCallback> callbacks = Maps.newConcurrentMap();
    private static final Map<UUID, Integer> ping = Maps.newConcurrentMap();
    private static final Map<UUID, Integer> lastReply = Maps.newConcurrentMap();

    public PingAdapter() {
        super(Atheneum.getInstance(), new PacketType[] { PacketType.Play.Server.KEEP_ALIVE, PacketType.Play.Client.KEEP_ALIVE });
    }

    public void onPacketSending(final PacketEvent event) {
        final int id = event.getPacket().getIntegers().read(0);
        PingAdapter.callbacks.put(event.getPlayer().getUniqueId(), new PingCallback(id) {
            @Override
            public void call() {
                int ping = (int)(System.currentTimeMillis() - this.getSendTime());
                PingAdapter.ping.put(event.getPlayer().getUniqueId(), ping);
                PingAdapter.lastReply.put(event.getPlayer().getUniqueId(), MinecraftServer.currentTick);
            }
        });
    }

    public void onPacketReceiving(final PacketEvent event) {
        final Iterator<Map.Entry<UUID, PingCallback>> iterator = PingAdapter.callbacks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PingCallback> entry = iterator.next();
            if (entry.getValue().getId() == (int)event.getPacket().getIntegers().read(0)) {
                entry.getValue().call();
                iterator.remove();
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        PingAdapter.ping.remove(event.getPlayer().getUniqueId());
        PingAdapter.lastReply.remove(event.getPlayer().getUniqueId());
        PingAdapter.callbacks.remove(event.getPlayer().getUniqueId());
    }

    public static int getAveragePing() {
        if (PingAdapter.ping.size() == 0) {
            return 0;
        }
        int x = 0;
        for (final int p : PingAdapter.ping.values()) {
            x += p;
        }
        return x / PingAdapter.ping.size();
    }

    public static Map<UUID, Integer> getPing() {
        return PingAdapter.ping;
    }

    public static Map<UUID, Integer> getLastReply() {
        return PingAdapter.lastReply;
    }

    private abstract static class PingCallback
    {
        private final long sendTime;
        private final int id;

        public abstract void call();

        @ConstructorProperties({ "id" })
        public PingCallback(final int id) {
            this.sendTime = System.currentTimeMillis();
            this.id = id;
        }

        public long getSendTime() {
            return this.sendTime;
        }

        public int getId() {
            return this.id;
        }
    }
}
