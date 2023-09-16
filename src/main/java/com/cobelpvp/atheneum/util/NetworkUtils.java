package com.cobelpvp.atheneum.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {
    private NetworkUtils() {
    }

    public static void writeVarInt(final DataOutputStream out, int value) throws IOException {
        for (int i = 0; i < 3; ++i) {
            if ((value & 0xFFFFFF80) == 0x0) {
                out.write(value);
                return;
            }
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }

    public static void writeString(final DataOutputStream out, final String str) throws IOException {
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writePacket(final DataOutputStream out, final byte[] bytes) throws IOException {
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static int readVarInt(final DataInputStream in) throws IOException {
        int value = 0;
        for (int i = 0; i < 3; ++i) {
            final int b = in.read();
            value |= (b & 0x7F) << i * 7;
            if ((b & 0x80) == 0x0) {
                break;
            }
        }
        return value;
    }

    public static String readString(final DataInputStream in) throws IOException {
        final int len = readVarInt(in);
        final byte[] bytes = new byte[len];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] readPacket(final DataInputStream in) throws IOException {
        final int len = readVarInt(in);
        final byte[] bytes = new byte[len];
        in.readFully(bytes);
        return bytes;
    }
}
