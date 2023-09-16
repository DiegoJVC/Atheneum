package com.cobelpvp.atheneum.nametag;

import com.cobelpvp.atheneum.packet.ScoreboardTeamPacketMod;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import lombok.Getter;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class TeamsNametagHandler {

    @Getter
    private static Map<String, Map<String, NametagInfo>> nametagMap = new ConcurrentHashMap<>();
    @Getter
    private static List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList());
    @Getter
    private static int teamCreateIndex = 1;
    @Getter
    private static List<NametagProvider> providers = new ArrayList();
    @Getter
    private static boolean nametagRestrictionEnabled = false;
    @Getter
    private static String nametagRestrictBypass = "";
    @Getter
    private static boolean initiated = false;
    @Getter
    private static boolean async = true;
    @Getter
    private static int updateInterval = 2;

    public static void init() {
        if (!Atheneum.getInstance().getConfig().getBoolean("disableNametags", false)) {
            Preconditions.checkState(!initiated);
            initiated = true;
            nametagRestrictionEnabled = Atheneum.getInstance().getConfig().getBoolean("NametagPacketRestriction.Enabled", false);
            nametagRestrictBypass = Atheneum.getInstance().getConfig().getString("NametagPacketRestriction.BypassPrefix").replace("&", "§");
            (new NametagThread()).start();
            Atheneum.getInstance().getServer().getPluginManager().registerEvents(new NametagListener(), Atheneum.getInstance());
            registerProvider(new NametagProvider.DefaultNametagProvider());
        }
    }

    protected static NametagInfo getOrCreate(final String prefix, final String suffix) {
        for (final NametagInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return teamInfo;
            }
        }
        final NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);
        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        for (final Player player : Atheneum.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }
        return newTeam;
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);
        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public static void reloadPlayer(final Player toRefresh, final Player refreshFor) {
        final NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);
        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public static void reloadOthersFor(final Player refreshFor) {
        for (final Player toRefresh : Atheneum.getInstance().getServer().getOnlinePlayers()) {
            if (refreshFor == toRefresh) {
                continue;
            }
            reloadPlayer(toRefresh, refreshFor);
        }
    }

    protected static void initiatePlayer(final Player player) {
        for (final NametagInfo teamInfo : registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }
    }

    public static void registerProvider(final NametagProvider newProvider) {
        providers.add(newProvider);
        Collections.sort(providers, new Comparator<NametagProvider>() {
            @Override
            public int compare(final NametagProvider a, final NametagProvider b) {
                return Ints.compare(b.getWeight(), a.getWeight());
            }
        });
    }

    protected static void applyUpdate(final NametagUpdate nametagUpdate) {
        final Player toRefreshPlayer = Atheneum.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());
        if (toRefreshPlayer == null) {
            return;
        }
        if (nametagUpdate.getRefreshFor() == null) {
            for (final Player refreshFor : Atheneum.getInstance().getServer().getOnlinePlayers()) {
                reloadPlayerInternal(toRefreshPlayer, refreshFor);
            }
        } else {
            final Player refreshForPlayer = Atheneum.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());
            if (refreshForPlayer != null) {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    protected static void reloadPlayerInternal(final Player toRefresh, final Player refreshFor) {
        if (!refreshFor.hasMetadata("AtheneumNametag-LoggedIn")) {
            return;
        }
        NametagInfo provided = null;
        for (int providerIndex = 0; provided == null; provided = providers.get(providerIndex++).fetchNametag(toRefresh, refreshFor)) {
        }
        if (((CraftPlayer) refreshFor).getHandle().playerConnection.networkManager.getVersion() > 5 && nametagRestrictionEnabled) {
            final String prefix = provided.getPrefix();
            if (prefix != null && !prefix.equalsIgnoreCase(nametagRestrictBypass)) {
                return;
            }
        }
        Map<String, NametagInfo> teamInfoMap = new HashMap<String, NametagInfo>();
        if (nametagMap.containsKey(refreshFor.getName())) {
            teamInfoMap = nametagMap.get(refreshFor.getName());
        }
        new ScoreboardTeamPacketMod(provided.getName(), Arrays.asList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        nametagMap.put(refreshFor.getName(), teamInfoMap);
    }

    protected static Map<String, Map<String, NametagInfo>> getTeamMap() {
        return nametagMap;
    }

    public static boolean isNametagRestrictionEnabled() {
        return nametagRestrictionEnabled;
    }

    public static void setNametagRestrictionEnabled(boolean nametagRestrictionEnabled) {
        nametagRestrictionEnabled = nametagRestrictionEnabled;
    }

    public static String getNametagRestrictBypass() {
        return nametagRestrictBypass;
    }

    public static void setNametagRestrictBypass(String nametagRestrictBypass) {
        nametagRestrictBypass = nametagRestrictBypass;
    }

    public static boolean isInitiated() {
        return initiated;
    }

    public static boolean isAsync() {
        return async;
    }

    public static void setAsync(boolean async) {
        async = async;
    }

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        updateInterval = updateInterval;
    }
}
