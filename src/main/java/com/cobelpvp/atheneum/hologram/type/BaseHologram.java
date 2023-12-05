package com.cobelpvp.atheneum.hologram.type;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.hologram.builder.HologramBuilder;
import com.cobelpvp.atheneum.hologram.construct.Hologram;
import com.cobelpvp.atheneum.hologram.construct.HologramLine;
import com.cobelpvp.atheneum.hologram.packet.HologramPacket;
import com.cobelpvp.atheneum.hologram.packet.HologramPacketProvider;
import com.cobelpvp.atheneum.hologram.packet.v1_7.Minecraft17HologramPacketProvider;
import com.cobelpvp.atheneum.hologram.packet.v1_8.Minecraft18HologramPacketProvider;
import com.cobelpvp.atheneum.tab.TabUtils;
import com.cobelpvp.atheneum.util.Pair;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BaseHologram implements Hologram {
    private int id;
    private Location location;
    private Collection<UUID> viewers;
    private final Set<UUID> currentWatchers;
    private List<HologramLine> lines;
    private List<HologramLine> lastLines;

    public Location getLocation() {
        return this.location;
    }

    public Collection<UUID> getViewers() {
        return this.viewers;
    }

    public Set<UUID> getCurrentWatchers() {
        return this.currentWatchers;
    }

    public BaseHologram(HologramBuilder builder) {
        this(builder, Atheneum.getInstance().getHologramHandler().createId());
    }

    public BaseHologram(HologramBuilder builder, int id) {
        this.currentWatchers = new HashSet();
        this.lines = new ArrayList();
        this.lastLines = new ArrayList();
        if (builder.getLocation() == null) {
            throw new IllegalArgumentException("Please provide a location for the hologram using HologramBuilder#at(Location)");
        } else {
            this.id = id;
            this.viewers = builder.getViewers();
            this.location = builder.getLocation();
            builder.getLines().forEach((line) -> {
                this.lines.add(new HologramLine(line));
            });
        }
    }

    public int id() {
        return this.id;
    }

    public void send() {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null)
            viewers = (Collection<UUID>)Atheneum.getInstance().getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
        viewers.stream().filter(viewer -> (Atheneum.getInstance().getServer().getPlayer(viewer) != null)).map(Atheneum.getInstance().getServer()::getPlayer).forEach(this::show);
        Atheneum.getInstance().getHologramHandler().register(this);
    }

    public void destroy() {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null)
            viewers = (Collection<UUID>)Atheneum.getInstance().getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
        viewers.stream().filter(viewer -> (Atheneum.getInstance().getServer().getPlayer(viewer) != null)).map(Atheneum.getInstance().getServer()::getPlayer).forEach(this::destroy0);
        if (this.viewers != null)
            this.viewers.clear();
        Atheneum.getInstance().getHologramHandler().unRegister(this);
    }

    public void delete() {
        this.destroy();
        Atheneum.getInstance().getHologramHandler().unRegister(this);
    }

    public void move(Location location) {
        this.location = location;
        this.update();
        Atheneum.getInstance().getHologramHandler().save();
    }

    public void addLines(String... lines) {
        String[] var2 = lines;
        int var3 = lines.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String line = var2[var4];
            this.lines.add(new HologramLine(line));
        }

        this.update();
    }

    public void removeLine(int paramVarArgs) {
        this.lines.remove(paramVarArgs);
        this.update();
    }

    public void setLine(int index, String line) {
        if (index > this.lines.size() - 1) {
            this.lines.add(new HologramLine(line));
        } else if (this.lines.get(index) != null) {
            ((HologramLine)this.lines.get(index)).setText(line);
        } else {
            this.lines.set(index, new HologramLine(line));
        }

        this.update();
    }

    public void setLines(Collection<String> lines) {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null)
            viewers = (Collection<UUID>)Atheneum.getInstance().getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
        viewers.stream().filter(viewer -> (Atheneum.getInstance().getServer().getPlayer(viewer) != null)).map(Atheneum.getInstance().getServer()::getPlayer).forEach(this::destroy0);
        this.lines.clear();
        lines.forEach(line -> this.lines.add(new HologramLine(line)));
        update();
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList();
        Iterator var2 = this.lines.iterator();

        while(var2.hasNext()) {
            HologramLine line = (HologramLine)var2.next();
            lines.add(line.getText());
        }

        return lines;
    }

    public SerializedHologram toSerializedHologram() {
        return new SerializedHologram(this.id, this.location, this.getLines());
    }

    public List<HologramLine> getRawLines() {
        return this.lines;
    }

    public void show(Player player) {
        if (player.getLocation().getWorld().equals(this.location.getWorld())) {
            Location first = this.location.clone().add(0.0, (double)this.lines.size() * 0.23, 0.0);
            Iterator var3 = this.lines.iterator();

            while(var3.hasNext()) {
                HologramLine line = (HologramLine)var3.next();
                this.showLine(player, first.clone(), line);
                first.subtract(0.0, 0.23, 0.0);
            }

            this.currentWatchers.add(player.getUniqueId());
        }
    }

    private Pair<Integer, Integer> showLine(Player player, Location loc, HologramLine line) {
        HologramPacketProvider packetProvider = this.getPacketProviderForPlayer(player);
        HologramPacket hologramPacket = packetProvider.getPacketsFor(loc, line);
        if (hologramPacket != null) {
            hologramPacket.sendToPlayer(player);
            return new Pair(hologramPacket.getEntityIds().get(0), hologramPacket.getEntityIds().get(1));
        } else {
            return null;
        }
    }

    public void destroy0(Player player) {
        List<Integer> ints = new ArrayList();
        Iterator var3 = this.lines.iterator();

        while(var3.hasNext()) {
            HologramLine line = (HologramLine)var3.next();
            if (line.getHorseId() == -1337) {
                ints.add(line.getSkullId());
            } else {
                ints.add(line.getSkullId());
                ints.add(line.getHorseId());
            }
        }

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.convertIntegers(ints));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        this.currentWatchers.remove(player.getUniqueId());
    }

    private int[] convertIntegers(List<Integer> integers) {
        int[] toReturn = new int[integers.size()];

        for(int i = 0; i < toReturn.length; ++i) {
            toReturn[i] = (Integer)integers.get(i);
        }

        return toReturn;
    }

    public void update() {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null)
            viewers = (Collection<UUID>)Atheneum.getInstance().getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
        viewers.stream().filter(viewer -> (Atheneum.getInstance().getServer().getPlayer(viewer) != null)).map(Atheneum.getInstance().getServer()::getPlayer).forEach(this::update);
        this.lastLines.addAll(this.lines);
    }

    public void update(Player player) {
        if (player.getLocation().getWorld().equals(this.location.getWorld())) {
            System.out.println("Updating hologram for " + player.getName());
            if (this.lastLines.size() != this.lines.size()) {
                this.destroy0(player);
                this.show(player);
                System.out.println(1);
            } else {
                for(int index = 0; index < this.getRawLines().size(); ++index) {
                    HologramLine line = (HologramLine)this.getRawLines().get(index);
                    String text = ChatColor.translateAlternateColorCodes('&', line.getText());
                    boolean is18 = TabUtils.is18(player);

                    try {
                        PacketContainer container = new PacketContainer(com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA);
                        container.getIntegers().write(0, is18 ? line.getSkullId() : line.getHorseId());
                        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                        wrappedDataWatcher.setObject(is18 ? 2 : 10, text);
                        List<WrappedWatchableObject> watchableObjects = Arrays.asList(Iterators.toArray(wrappedDataWatcher.iterator(), WrappedWatchableObject.class));
                        container.getWatchableCollectionModifier().write(0, watchableObjects);

                        try {
                            System.out.println(2);
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
                        } catch (Exception var10) {
                        }
                    } catch (IndexOutOfBoundsException var11) {
                        this.destroy0(player);
                        this.show(player);
                        System.out.println(3);
                    }
                }

            }
        }
    }

    private HologramPacketProvider getPacketProviderForPlayer(Player player) {
        return (HologramPacketProvider)(((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 5 ? new Minecraft18HologramPacketProvider() : new Minecraft17HologramPacketProvider());
    }
}
