package com.cobelpvp.atheneum.deathmessage.damage;

import java.util.UUID;

public abstract class PlayerDamage extends Damage
{
    private final UUID damager;

    public PlayerDamage(final UUID damaged, final double damage, final UUID damager) {
        super(damaged, damage);
        this.damager = damager;
    }

    public UUID getDamager() {
        return this.damager;
    }
}
