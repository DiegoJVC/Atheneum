package com.cobelpvp.atheneum.hologram.packet;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.beans.ConstructorProperties;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.bukkit.entity.Player;

public class HologramPacket {
    private List<PacketContainer> packets;
    private List<Integer> entityIds;

    public void sendToPlayer(Player player) {
        this.packets.forEach((packet) -> {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (InvocationTargetException var3) {
                var3.printStackTrace();
            }

        });
    }

    @ConstructorProperties({"packets", "entityIds"})
    public HologramPacket(List<PacketContainer> packets, List<Integer> entityIds) {
        this.packets = packets;
        this.entityIds = entityIds;
    }

    public List<PacketContainer> getPackets() {
        return this.packets;
    }

    public List<Integer> getEntityIds() {
        return this.entityIds;
    }
}
