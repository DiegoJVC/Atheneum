package com.cobelpvp.atheneum.util;


import com.cobelpvp.atheneum.Atheneum;
import java.beans.ConstructorProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public final class PingUtils {
    private PingUtils() {
    }

    public static void ping(final String host, final int port, final Callback callback) {
        new PingTask(host, port, callback).run();
    }

    public interface Callback {
        void success(final PingResponse p0);

        void failure(final Exception p0);
    }

    public static class PingResponse {
        private Version version;
        private Players players;
        private String description;
        private String favicon;

        public Version getVersion() {
            return this.version;
        }

        public Players getPlayers() {
            return this.players;
        }

        public String getDescription() {
            return this.description;
        }

        public String getFavicon() {
            return this.favicon;
        }

        public static class Players {
            private int max;
            private int online;

            public int getMax() {
                return this.max;
            }

            public int getOnline() {
                return this.online;
            }
        }

        public static class Version {
            private String name;
            private int protocol;

            public String getName() {
                return this.name;
            }

            public int getProtocol() {
                return this.protocol;
            }
        }
    }

    private static class PingTask implements Runnable {
        private final String host;
        private final int port;
        private final Callback callback;

        @ConstructorProperties({"host", "port", "callback"})
        public PingTask(final String host, final int port, final Callback callback) {
            this.host = host;
            this.port = port;
            this.callback = callback;
        }

        @Override
        public void run() {
            try (final Socket socket = new Socket()) {
                final SocketAddress address = new InetSocketAddress(this.host, this.port);
                socket.connect(address, 5000);
                socket.setSoTimeout(5000);
                final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                final ByteArrayOutputStream handshake = new ByteArrayOutputStream();
                final DataOutputStream handshakeOut = new DataOutputStream(handshake);
                NetworkUtils.writeVarInt(handshakeOut, 0);
                NetworkUtils.writeVarInt(handshakeOut, 4);
                NetworkUtils.writeString(handshakeOut, this.host);
                handshakeOut.writeShort(this.port);
                NetworkUtils.writeVarInt(handshakeOut, 1);
                NetworkUtils.writePacket(out, handshake.toByteArray());
                final ByteArrayOutputStream status = new ByteArrayOutputStream();
                final DataOutputStream statusOut = new DataOutputStream(status);
                NetworkUtils.writeVarInt(statusOut, 0);
                NetworkUtils.writePacket(out, status.toByteArray());
                final DataInputStream in = new DataInputStream(socket.getInputStream());
                final byte[] response = NetworkUtils.readPacket(in);
                final DataInputStream responseIn = new DataInputStream(new ByteArrayInputStream(response));
                final int id = NetworkUtils.readVarInt(responseIn);
                if (id != 0) {
                    throw new Exception("Unexpected packet ID");
                }
                final String jsonResponse = NetworkUtils.readString(responseIn);
                this.callback.success((PingResponse) Atheneum.GSON.fromJson(jsonResponse, (Class) PingResponse.class));
            } catch (Exception e) {
                this.callback.failure(e);
            }
        }
    }
}
