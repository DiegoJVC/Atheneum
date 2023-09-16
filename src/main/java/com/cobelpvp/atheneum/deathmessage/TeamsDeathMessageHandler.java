package com.cobelpvp.atheneum.deathmessage;

import java.util.HashMap;
import java.util.ArrayList;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.deathmessage.damage.Damage;
import com.cobelpvp.atheneum.deathmessage.listener.DamageListener;
import com.cobelpvp.atheneum.deathmessage.listener.DeathListener;
import com.cobelpvp.atheneum.deathmessage.listener.DisconnectListener;
import com.cobelpvp.atheneum.deathmessage.tracker.*;
import com.cobelpvp.atheneum.deathmessage.tracker.*;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public final class TeamsDeathMessageHandler
{
    private static DeathMessageConfiguration configuration;
    private static Map<UUID, List<Damage>> damage;
    private static boolean initiated;

    private TeamsDeathMessageHandler() {
    }

    public static void init() {
        Preconditions.checkState(!TeamsDeathMessageHandler.initiated);
        TeamsDeathMessageHandler.initiated = true;
        final PluginManager pluginManager = Atheneum.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents((Listener)new DamageListener(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new DeathListener(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new DisconnectListener(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new GeneralTracker(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new PvPTracker(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new EntityTracker(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new FallTracker(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new ArrowTracker(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new VoidTracker(), (Plugin) Atheneum.getInstance());
        pluginManager.registerEvents((Listener)new BurnTracker(), (Plugin) Atheneum.getInstance());
    }

    public static List<Damage> getDamage(final Player player) {
        return (List<Damage>)(TeamsDeathMessageHandler.damage.containsKey(player.getUniqueId()) ? ((List<Damage>) TeamsDeathMessageHandler.damage.get(player.getUniqueId())) : ImmutableList.of());
    }

    public static void addDamage(final Player player, final Damage addedDamage) {
        TeamsDeathMessageHandler.damage.putIfAbsent(player.getUniqueId(), new ArrayList<Damage>());
        final List<Damage> damageList = TeamsDeathMessageHandler.damage.get(player.getUniqueId());
        while (damageList.size() > 30) {
            damageList.remove(0);
        }
        damageList.add(addedDamage);
    }

    public static void clearDamage(final Player player) {
        TeamsDeathMessageHandler.damage.remove(player.getUniqueId());
    }

    public static DeathMessageConfiguration getConfiguration() {
        return TeamsDeathMessageHandler.configuration;
    }

    public static void setConfiguration(final DeathMessageConfiguration configuration) {
        TeamsDeathMessageHandler.configuration = configuration;
    }

    static {
        TeamsDeathMessageHandler.configuration = DeathMessageConfiguration.DEFAULT_CONFIGURATION;
        TeamsDeathMessageHandler.damage = new HashMap<UUID, List<Damage>>();
        TeamsDeathMessageHandler.initiated = false;
    }
}
