package com.cobelpvp.atheneum;

import com.cobelpvp.atheneum.autoreboot.AutoRebootHandler;
import com.cobelpvp.atheneum.command.TeamsCommandHandler;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.event.HalfHourEvent;
import com.cobelpvp.atheneum.event.HourEvent;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.protocol.InventoryAdapter;
import com.cobelpvp.atheneum.protocol.LagCheck;
import com.cobelpvp.atheneum.protocol.PingAdapter;
import com.cobelpvp.atheneum.redis.RedisCommand;
import com.cobelpvp.atheneum.scoreboard.TeamsScoreboardHandler;
import com.cobelpvp.atheneum.serialization.*;
import com.cobelpvp.atheneum.util.ItemUtils;
import com.cobelpvp.atheneum.util.TPSUtils;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.atheneum.visibility.TeamsVisibilityHandler;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Setter;
import com.cobelpvp.atheneum.tab.TabAdapter;
import com.cobelpvp.atheneum.tab.TeamsTabHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class Atheneum extends JavaPlugin {

    public static boolean testing = false;

    @Getter private static Atheneum instance;
    @Getter private static Atheneum plugin;

    @Getter private long localRedisLastError;
    @Getter private long backboneRedisLastError;

    public static final Random RANDOM = new Random();

    public static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).setPrettyPrinting().serializeNulls().create();
    public static final Gson PLAIN_GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).serializeNulls().create();

    @Getter private JedisPool localJedisPool;
    @Getter private JedisPool backboneJedisPool;

    @Override
    public void onEnable() {
        instance = this;
        plugin = this;

        testing = this.getConfig().getBoolean("testing", false);

        // This save default configuration
        saveDefaultConfig();

        // Startup redis
        loadRedis();

        // Register all handlers
        registerTeamsHandlers();
        // Register tasks
        registerTasks();

        // This load item utilities
        ItemUtils.load();
        // This register bungeecord channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Setup hour events :)
        setupHourEvents();
    }

    @Override
    public void onDisable() {
        // Save economy for all players
        if (TeamsEconomyHandler.isInitiated()) TeamsEconomyHandler.saveAll();

        // This close the jedis pool
        this.localJedisPool.close();
        this.backboneJedisPool.close();
    }

    public void registerTeamsHandlers() {
        TeamsCommandHandler.init();
        TeamsScoreboardHandler.init();
        TeamsCommandHandler.registerAll(this);
        TeamsUUIDCache.init();
        TeamsNametagHandler.init();
        TeamsCommandHandler.init();
        TeamsCommandHandler.init();
        TeamsTabHandler.init();
        TeamsVisibilityHandler.init();
        AutoRebootHandler.init();
    }

    public void registerTasks() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSUtils(), 1L, 1L);

        new BukkitRunnable() {
            public void run() {
                if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
                    ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());
                    final PingAdapter ping = new PingAdapter();
                    ProtocolLibrary.getProtocolManager().addPacketListener(ping);
                    Bukkit.getPluginManager().registerEvents(ping, Atheneum.getInstance());
                    new LagCheck().runTaskTimerAsynchronously(Atheneum.getInstance(), 100L, 100L);
                    ProtocolLibrary.getProtocolManager().addPacketListener(new TabAdapter());
                }
            }
        }.runTaskLater(this, 1L);
    }

    public void loadRedis() {
        try {
            this.localJedisPool = new JedisPool(new JedisPoolConfig(), this.getConfig().getString("Redis.Host"), 6379, 20000, null, this.getConfig().getInt("Redis.DbId", 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), this.getConfig().getString("BackboneRedis.Host"), 6379, 20000, null, this.getConfig().getInt("BackboneRedis.DbId", 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T runRedisCommand(final RedisCommand<T> redisCommand) {
        if (Atheneum.testing) {
            return null;
        }
        Jedis jedis = this.localJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            this.localRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                this.localJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    public <T> T runBackboneRedisCommand(final RedisCommand<T> redisCommand) {
        if (Atheneum.testing) {
            return null;
        }
        Jedis jedis = this.backboneJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            this.backboneRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                this.backboneJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    private void setupHourEvents() {
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Atheneum - Hour Event Thread").setDaemon(true).build());
        final int minOfHour = Calendar.getInstance().get(12);
        final int minToHour = 60 - minOfHour;
        final int minToHalfHour = (minToHour >= 30) ? minToHour : (30 - minOfHour);
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(11)))), minToHour, 60L, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HalfHourEvent(Calendar.getInstance().get(11), Calendar.getInstance().get(12)))), minToHalfHour, 30L, TimeUnit.MINUTES);
    }
}
