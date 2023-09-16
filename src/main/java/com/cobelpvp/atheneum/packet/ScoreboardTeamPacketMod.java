package com.cobelpvp.atheneum.packet;

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

public class ScoreboardTeamPacketMod {

    private static Field aField;
    private static Field bField;
    private static Field cField;
    private static Field dField;
    private static Field eField;
    private static Field fField;
    private static Field gField;

    static {
        try {
            aField = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
            bField = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
            cField = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
            dField = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
            eField = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
            fField = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
            gField = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
            aField.setAccessible(true);
            bField.setAccessible(true);
            cField.setAccessible(true);
            dField.setAccessible(true);
            eField.setAccessible(true);
            fField.setAccessible(true);
            gField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final PacketPlayOutScoreboardTeam packet;

    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection players, int paramInt) {
        this.packet = new PacketPlayOutScoreboardTeam();
        try {
            ScoreboardTeamPacketMod.aField.set(this.packet, name);
            ScoreboardTeamPacketMod.fField.set(this.packet, paramInt);
            if (paramInt == 0 || paramInt == 2) {
                ScoreboardTeamPacketMod.bField.set(this.packet, name);
                ScoreboardTeamPacketMod.cField.set(this.packet, prefix);
                ScoreboardTeamPacketMod.dField.set(this.packet, suffix);
                ScoreboardTeamPacketMod.gField.set(this.packet, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paramInt == 0) {
            this.addAll(players);
        }
    }

    public ScoreboardTeamPacketMod(String name, Collection players, int paramInt) {
        packet = new PacketPlayOutScoreboardTeam();
        try {
            gField.set(this.packet, 3);
            aField.set(this.packet, name);
            fField.set(this.packet, paramInt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(this.packet);
    }

    private void addAll(Collection col) {
        if (col == null) {
            return;
        }
        try {
            ((Collection) eField.get(this.packet)).addAll(col);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
