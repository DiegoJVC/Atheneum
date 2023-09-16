package com.cobelpvp.atheneum.hologram.packet;

import com.cobelpvp.atheneum.hologram.construct.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider {

	HologramPacket getPacketsFor(Location location, HologramLine line);

	//TODO: add more versions
}
