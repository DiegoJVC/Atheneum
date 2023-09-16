package com.cobelpvp.atheneum.tab;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.redis.RedisCommand;
import com.google.common.base.Preconditions;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.HttpAuthenticationService;
import net.minecraft.util.com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.net.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class TeamsTabHandler {
    private static final AtomicReference<Object> propertyMapSerializer;
    private static final AtomicReference<Object> defaultPropertyMap;
    private static boolean initiated;
    private static LayoutProvider layoutProvider;
    private static Map<String, TeamsTab> tabs;

    static {
        TeamsTabHandler.initiated = false;
        propertyMapSerializer = new AtomicReference<Object>();
        defaultPropertyMap = new AtomicReference<Object>();
        TeamsTabHandler.tabs = new ConcurrentHashMap<String, TeamsTab>();
    }

    public static void init() {
        if (Atheneum.getInstance().getConfig().getBoolean("disableTab", false)) {
            return;
        }
        Preconditions.checkState(!TeamsTabHandler.initiated);
        TeamsTabHandler.initiated = true;
        getDefaultPropertyMap();
        new TabThread().start();
        Atheneum.getInstance().getServer().getPluginManager().registerEvents(new TabListener(), Atheneum.getInstance());
    }

    protected static void addPlayer(final Player player) {
        TeamsTabHandler.tabs.put(player.getName(), new TeamsTab(player));
    }

    protected static void updatePlayer(final Player player) {
        if (TeamsTabHandler.tabs.containsKey(player.getName())) {
            TeamsTabHandler.tabs.get(player.getName()).update();
        }
    }

    protected static void removePlayer(final Player player) {
        TeamsTabHandler.tabs.remove(player.getName());
    }

    private static PropertyMap fetchSkin() {
        final String propertyMap = Atheneum.getInstance().runBackboneRedisCommand(new RedisCommand<String>() {
            @Override
            public String execute(final Jedis redis) {
                return redis.get("propertyMap");
            }
        });
        if (propertyMap != null && !propertyMap.isEmpty()) {
            Bukkit.getLogger().info("Using cached PropertyMap for skin...");
            final JsonArray jsonObject = new JsonParser().parse(propertyMap).getAsJsonArray();
            return getPropertyMapSerializer().deserialize(jsonObject, null, null);
        }
        final GameProfile profile = new GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "bananasquad");
        final HttpAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
        final GameProfile profile2 = sessionService.fillProfileProperties(profile, true);
        final PropertyMap localPropertyMap = profile2.getProperties();
        Atheneum.getInstance().runBackboneRedisCommand(new RedisCommand<Object>() {
            @Override
            public Object execute(final Jedis redis) {
                Bukkit.getLogger().info("Caching PropertyMap for skin...");
                redis.setex("propertyMap", 3600, TeamsTabHandler.getPropertyMapSerializer().serialize(localPropertyMap, null, null).toString());
                return null;
            }
        });
        return localPropertyMap;
    }

    public static PropertyMap.Serializer getPropertyMapSerializer() {
        Object value = TeamsTabHandler.propertyMapSerializer.get();
        if (value == null) {
            synchronized (TeamsTabHandler.propertyMapSerializer) {
                value = TeamsTabHandler.propertyMapSerializer.get();
                if (value == null) {
                    final PropertyMap.Serializer actualValue = new PropertyMap.Serializer();
                    value = ((actualValue == null) ? TeamsTabHandler.propertyMapSerializer : actualValue);
                    TeamsTabHandler.propertyMapSerializer.set(value);
                }
            }
        }
        return (PropertyMap.Serializer) ((value == TeamsTabHandler.propertyMapSerializer) ? null : value);
    }

    public static PropertyMap getDefaultPropertyMap() {
        Object value = TeamsTabHandler.defaultPropertyMap.get();
        if (value == null) {
            synchronized (TeamsTabHandler.defaultPropertyMap) {
                value = TeamsTabHandler.defaultPropertyMap.get();
                if (value == null) {
                    final PropertyMap actualValue = fetchSkin();
                    value = ((actualValue == null) ? TeamsTabHandler.defaultPropertyMap : actualValue);
                    TeamsTabHandler.defaultPropertyMap.set(value);
                }
            }
        }
        return (PropertyMap) ((value == TeamsTabHandler.defaultPropertyMap) ? null : value);
    }

    public static LayoutProvider getLayoutProvider() {
        return TeamsTabHandler.layoutProvider;
    }

    public static void setLayoutProvider(final LayoutProvider provider) {
        TeamsTabHandler.layoutProvider = provider;
    }

    public static Map<String, TeamsTab> getTabs() {
        return TeamsTabHandler.tabs;
    }
}
