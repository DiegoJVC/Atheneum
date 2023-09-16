package com.cobelpvp.atheneum.economy;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeamsEconomyHandler {
    private static boolean initiated;
    private static Map<UUID, Double> balances;

    static {
        TeamsEconomyHandler.initiated = false;
        TeamsEconomyHandler.balances = new HashMap<UUID, Double>();
    }

    private TeamsEconomyHandler() {
    }

    public static void init() {
        if (initiated) {
            return;
        }

        if (Bukkit.getServerName().contains("Hub")) return;

        initiated = true;
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(final PlayerQuitEvent event) {
                save(event.getPlayer().getUniqueId());
            }
        }, Atheneum.getInstance());
        Atheneum.getInstance().runRedisCommand((redis) -> {
            for (String key : redis.keys("balance.*")) {
                UUID uuid = UUID.fromString(key.substring(8));
                balances.put(uuid, Double.parseDouble(redis.get(key)));
            }
            return null;
        });
    }

    public static void setBalance(final UUID uuid, final double balance) {
        TeamsEconomyHandler.balances.put(uuid, balance);
        Bukkit.getScheduler().runTaskAsynchronously(Atheneum.getInstance(), () -> save(uuid));
    }

    public static double getBalance(final UUID uuid) {
        if (!TeamsEconomyHandler.balances.containsKey(uuid)) {
            load(uuid);
        }
        return TeamsEconomyHandler.balances.get(uuid);
    }

    public static void withdraw(final UUID uuid, final double amount) {
        setBalance(uuid, getBalance(uuid) - amount);
        Bukkit.getScheduler().runTaskAsynchronously(Atheneum.getInstance(), () -> save(uuid));
    }

    public static void deposit(final UUID uuid, final double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
        Bukkit.getScheduler().runTaskAsynchronously(Atheneum.getInstance(), () -> save(uuid));
    }

    private static void load(final UUID uuid) {
        Atheneum.getInstance().runRedisCommand(redis -> {
            if (redis.exists("balance." + uuid.toString())) {
                TeamsEconomyHandler.balances.put(uuid, Double.parseDouble(redis.get("balance." + uuid.toString())));
            } else {
                TeamsEconomyHandler.balances.put(uuid, 0.0);
            }
            return null;
        });
    }

    private static void save(final UUID uuid) {
        Atheneum.getInstance().runRedisCommand(redis -> redis.set("balance." + uuid.toString(), String.valueOf(getBalance(uuid))));
    }

    public static void saveAll() {
        Atheneum.getInstance().runRedisCommand((redis) -> {
            Iterator var1 = balances.entrySet().iterator();

            while (var1.hasNext()) {
                Map.Entry<UUID, Double> entry = (Map.Entry) var1.next();
                redis.set("balance." + entry.getKey().toString(), String.valueOf(entry.getValue()));
            }

            return null;
        });
    }

    public static boolean isInitiated() {
        return TeamsEconomyHandler.initiated;
    }

    public static Map<UUID, Double> getBalances() {
        return TeamsEconomyHandler.balances;
    }
}
