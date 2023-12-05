package com.cobelpvp.atheneum.hologram;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.hologram.builder.HologramBuilder;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import com.cobelpvp.atheneum.hologram.listener.HologramListener;
import com.cobelpvp.atheneum.hologram.type.SerializedHologram;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class HologramHandler {
    private Map<Integer, Hologram> cache = new HashMap<>();

    public HologramHandler() {
        File file = new File(Atheneum.getInstance().getDataFolder(), "holograms.json");
        List<SerializedHologram> holograms = null;
        if (file.exists())
            try {
                holograms = (List<SerializedHologram>)Atheneum.GSON.fromJson(FileUtils.readFileToString(file), (new TypeToken<List<SerializedHologram>>() {

                }).getType());
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        if (holograms != null)
            holograms.forEach(hologram -> {
                Hologram var10000 = this.cache.put(Integer.valueOf(hologram.getId()), createHologram().addLines(hologram.getLines()).at(hologram.getLocation()).build(hologram.getId()));
            });
        Atheneum.getInstance().getServer().getPluginManager().registerEvents((Listener)new HologramListener(), (Plugin)Atheneum.getInstance());
        Atheneum.getInstance().getTeamsCommandHandler().registerPackage((Plugin)Atheneum.getInstance(), "com.cobelpvp.atheneum.hologram.command");
    }

    public void register(Hologram hologram) {
        this.cache.put(Integer.valueOf(hologram.id()), hologram);
        save();
    }

    public void unRegister(Hologram hologram) {
        this.cache.remove(Integer.valueOf(hologram.id()));
        save();
    }

    public void save() {
        List<SerializedHologram> toSerialize = (List<SerializedHologram>)this.cache.values().stream().map(Hologram::toSerializedHologram).collect(Collectors.toList());
        File file = new File(Atheneum.getInstance().getDataFolder(), "holograms.json");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        try {
            FileUtils.write(file, Atheneum.GSON.toJson(toSerialize));
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    public int createId() {
        int id;
        for (id = this.cache.size() + 1; this.cache.get(Integer.valueOf(id)) != null; id++);
        return id;
    }

    public HologramBuilder forPlayer(Player player) {
        return new HologramBuilder(Collections.singleton(player.getUniqueId()));
    }

    public HologramBuilder forPlayers(Collection<Player> players) {
        return (players == null) ? new HologramBuilder(null) : new HologramBuilder((Collection)players.stream().map(Entity::getUniqueId).collect(Collectors.toList()));
    }

    public HologramBuilder createHologram() {
        return forPlayers(null);
    }

    public Hologram fromId(int id) {
        return this.cache.get(Integer.valueOf(id));
    }

    public Map<Integer, Hologram> getCache() {
        return this.cache;
    }
}
