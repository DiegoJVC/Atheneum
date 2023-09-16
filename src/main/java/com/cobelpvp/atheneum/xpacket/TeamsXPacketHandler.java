package com.cobelpvp.atheneum.xpacket;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.bukkit.configuration.file.FileConfiguration;

public final class TeamsXPacketHandler
{
    private static final String GLOBAL_MESSAGE_CHANNEL = "XPacket:All";
    static final String PACKET_MESSAGE_DIVIDER = "||";

    public static void init() {
        final FileConfiguration config = Atheneum.getInstance().getConfig();
        final String localHost = config.getString("Redis.Host");
        final int localDb = config.getInt("Redis.DbId", 0);
        final String remoteHost = config.getString("BackboneRedis.Host");
        final int remoteDb = config.getInt("BackboneRedis.DbId", 0);
        final boolean sameServer = localHost.equalsIgnoreCase(remoteHost) && localDb == remoteDb;
        connectToServer(Atheneum.getInstance().getLocalJedisPool());
        if (!sameServer) {
            connectToServer(Atheneum.getInstance().getBackboneJedisPool());
        }
    }

    public static void connectToServer(JedisPool connectTo) {
        if (!Atheneum.testing) {
            Thread subscribeThread = new Thread(() -> {
                while(Atheneum.getInstance().isEnabled()) {
                    try {
                        Jedis jedis = connectTo.getResource();
                        Throwable var2 = null;

                        try {
                            JedisPubSub pubSub = new XPacketPubSub();
                            String channel = "XPacket:All";
                            jedis.subscribe(pubSub, new String[]{channel});
                        } catch (Throwable var13) {
                            var2 = var13;
                            throw var13;
                        } finally {
                            if (jedis != null) {
                                if (var2 != null) {
                                    try {
                                        jedis.close();
                                    } catch (Throwable var12) {
                                        var2.addSuppressed(var12);
                                    }
                                } else {
                                    jedis.close();
                                }
                            }

                        }
                    } catch (Exception var15) {
                        var15.printStackTrace();
                    }
                }

            }, "Atheneum - XPacket Subscribe Thread");
            subscribeThread.setDaemon(true);
            subscribeThread.start();
        }
    }

    public static void sendToAll(final XPacket packet) {
        send(packet, Atheneum.getInstance().getBackboneJedisPool());
    }

    public static void sendToAllViaLocal(final XPacket packet) {
        send(packet, Atheneum.getInstance().getLocalJedisPool());
    }

    public static void send(XPacket packet, JedisPool sendOn) {
        if (Atheneum.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(Atheneum.getInstance(), () -> {
                Jedis jedis = sendOn.getResource();
                Throwable var3 = null;

                try {
                    String encodedPacket = packet.getClass().getName() + "||" + Atheneum.PLAIN_GSON.toJson(packet);
                    jedis.publish("XPacket:All", encodedPacket);
                } catch (Throwable var12) {
                    var3 = var12;
                    throw var12;
                } finally {
                    if (jedis != null) {
                        if (var3 != null) {
                            try {
                                jedis.close();
                            } catch (Throwable var11) {
                                var3.addSuppressed(var11);
                            }
                        } else {
                            jedis.close();
                        }
                    }

                }

            });
        }
    }

    private TeamsXPacketHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
