package com.cobelpvp.atheneum.protocol;

import java.util.HashSet;
import com.comphenix.protocol.events.PacketContainer;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.protocol.event.PlayerCloseInventoryEvent;
import com.cobelpvp.atheneum.protocol.event.PlayerOpenInventoryEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import java.util.UUID;
import java.util.Set;
import com.comphenix.protocol.events.PacketAdapter;

public class InventoryAdapter extends PacketAdapter
{
    private static Set<UUID> currentlyOpen = InventoryAdapter.currentlyOpen = new HashSet<UUID>();

    public InventoryAdapter() {
        super(Atheneum.getInstance(), new PacketType[] { PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.CLOSE_WINDOW });
    }

    public void onPacketReceiving(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        if (packet.getType() == PacketType.Play.Client.CLIENT_COMMAND && packet.getClientCommands().size() != 0 && packet.getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if (!InventoryAdapter.currentlyOpen.contains(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Atheneum.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new PlayerOpenInventoryEvent(player)));
            }
            InventoryAdapter.currentlyOpen.add(player.getUniqueId());
        }
        else if (packet.getType() == PacketType.Play.Client.CLOSE_WINDOW) {
            if (InventoryAdapter.currentlyOpen.contains(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Atheneum.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new PlayerCloseInventoryEvent(player)));
            }
            InventoryAdapter.currentlyOpen.remove(player.getUniqueId());
        }
    }

    public static Set<UUID> getCurrentlyOpen() {
        return InventoryAdapter.currentlyOpen;
    }
}
