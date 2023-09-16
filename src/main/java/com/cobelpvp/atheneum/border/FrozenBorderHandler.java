package com.cobelpvp.atheneum.border;

import com.cobelpvp.atheneum.Atheneum;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class FrozenBorderHandler {

    private static final Map<World, Border> borderMap = new HashMap<>();
    private static boolean initiated = false;

    private FrozenBorderHandler() {
    }

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        Bukkit.getPluginManager().registerEvents(new BorderListener(), Atheneum.getInstance());
        Bukkit.getPluginManager().registerEvents(new InternalBorderListener(), Atheneum.getInstance());
        new EnsureInsideRunnable().runTaskTimer(Atheneum.getInstance(), 5L, 5L);
    }

    public static Border getBorderForWorld(World world) {
        return borderMap.get(world);
    }

    static void addBorder(Border border) {
        borderMap.put(border.getOrigin().getWorld(), border);
    }
}

