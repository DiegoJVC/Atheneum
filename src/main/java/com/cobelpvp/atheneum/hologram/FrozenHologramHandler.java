package com.cobelpvp.atheneum.hologram;

import java.util.*;
import java.util.stream.Collectors;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.hologram.builder.HologramBuilder;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import com.cobelpvp.atheneum.hologram.listener.HologramListener;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FrozenHologramHandler {

	@Getter private static Set<Hologram> cache = new HashSet<>();

	public static void init() {
		Bukkit.getServer().getPluginManager().registerEvents(new HologramListener(), Atheneum.getInstance());
	}

	public static HologramBuilder forPlayer(Player player) {
		return new HologramBuilder(Collections.singleton(player.getUniqueId()));
	}

	public static HologramBuilder forPlayers(Collection<Player> players) {

		if (players == null) {
			return new HologramBuilder(null);
		}

		return new HologramBuilder(players.stream().map(Player::getUniqueId).collect(Collectors.toList()));
	}

	public static HologramBuilder createHologram() {
		return forPlayers(null);
	}

}
