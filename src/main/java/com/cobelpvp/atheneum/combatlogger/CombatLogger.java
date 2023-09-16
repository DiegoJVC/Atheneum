package com.cobelpvp.atheneum.combatlogger;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.Location;
import java.util.Collection;
import org.bukkit.ChatColor;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class CombatLogger {

    public static final String COMBAT_LOGGER_METADATA = "Libs-CombatLogger";
    private String playerName;
    private UUID playerUuid;
    private ItemStack[] armor;
    private ItemStack[] inventory;
    private double health;
    private Set<PotionEffect> effects;
    private long despawnTime;
    private EntityType entityType;
    private String nameFormat;
    private CombatLoggerAdapter eventAdapter;
    private LivingEntity spawnedEntity;

    public CombatLogger(Player player, long time, TimeUnit unit) {
        this.health = 20.0;
        this.effects = new HashSet<PotionEffect>();
        this.entityType = EntityType.VILLAGER;
        this.nameFormat = ChatColor.YELLOW + "%s";
        if (!TeamsCombatLoggerHandler.isInitiated()) {
            throw new IllegalArgumentException("TeamsCombatLoggerHandler has not been initiated!");
        }
        this.playerName = player.getName();
        this.playerUuid = player.getUniqueId();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.despawnTime = unit.toSeconds(time);
    }

    public CombatLogger setDespawnTime(long time, final TimeUnit unit) {
        this.despawnTime = unit.toSeconds(time);
        return this;
    }

    public CombatLogger setEntityType(EntityType entityType) {
        if (!entityType.isAlive() && !entityType.isSpawnable()) {
            throw new IllegalArgumentException("EntityType must be living and spawnable!");
        }
        this.entityType = entityType;
        return this;
    }

    public CombatLogger setHealth(double health) {
        this.health = health;
        return this;
    }

    public CombatLogger setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public CombatLogger setPotionEffects(Collection<PotionEffect> effects) {
        this.effects.addAll(effects);
        return this;
    }

    public CombatLogger setAdapter(CombatLoggerAdapter adapter) {
        this.eventAdapter = adapter;
        return this;
    }

    public LivingEntity spawn(Location location) {
        final LivingEntity entity = (LivingEntity)location.getWorld().spawnEntity(location, this.entityType);
        entity.setMetadata("Libs-CombatLogger", new FixedMetadataValue(Atheneum.getInstance(), "001100010010011110100001"));
        TeamsCombatLoggerHandler.getCombatLoggerMap().put(entity.getUniqueId(), this);
        TeamsCombatLoggerHandler.getCombatLoggerMap().put(this.playerUuid, this);
        entity.setCustomName(String.format(this.nameFormat, this.playerName));
        entity.setCustomNameVisible(true);
        entity.setCanPickupItems(false);
        entity.addPotionEffects((Collection)this.effects);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100), true);
        entity.setMaxHealth(this.health + 2.0);
        entity.setHealth(this.health);
        new BukkitRunnable() {
            public void run() {
                if (!entity.isDead() && entity.isValid()) {
                    entity.remove();
                    TeamsCombatLoggerHandler.getCombatLoggerMap().remove(entity.getUniqueId());
                    TeamsCombatLoggerHandler.getCombatLoggerMap().remove(CombatLogger.this.playerUuid);
                }
            }
        }.runTaskLater((Plugin) Atheneum.getInstance(), this.despawnTime * 20L);
        return this.spawnedEntity = entity;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public double getHealth() {
        return this.health;
    }

    public Set<PotionEffect> getEffects() {
        return this.effects;
    }

    public long getDespawnTime() {
        return this.despawnTime;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public String getNameFormat() {
        return this.nameFormat;
    }

    public CombatLoggerAdapter getEventAdapter() {
        return this.eventAdapter;
    }

    public LivingEntity getSpawnedEntity() {
        return this.spawnedEntity;
    }
}
