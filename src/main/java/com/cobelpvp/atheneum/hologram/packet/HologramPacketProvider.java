package com.cobelpvp.atheneum.hologram.packet;

import com.cobelpvp.atheneum.hologram.construct.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider {
    HologramPacket getPacketsFor(Location var1, HologramLine var2);
}
