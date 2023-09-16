package com.cobelpvp.atheneum.deathmessage.damage;

import com.cobelpvp.atheneum.deathmessage.DeathMessageConfiguration;
import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;

import java.util.UUID;

public abstract class Damage
{
    private final UUID damaged;
    private final double damage;
    private final long time;

    public Damage(final UUID damaged, final double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public static String wrapName(final UUID player, final UUID wrapFor) {
        final DeathMessageConfiguration configuration = TeamsDeathMessageHandler.getConfiguration();
        return configuration.formatPlayerName(player, wrapFor);
    }

    public static String wrapName(final UUID player) {
        final DeathMessageConfiguration configuration = TeamsDeathMessageHandler.getConfiguration();
        return configuration.formatPlayerName(player);
    }

    public abstract String getDeathMessage(final UUID p0);

    public long getTimeAgoMillis() {
        return System.currentTimeMillis() - this.time;
    }

    public UUID getDamaged() {
        return this.damaged;
    }

    public double getDamage() {
        return this.damage;
    }

    public long getTime() {
        return this.time;
    }
}
