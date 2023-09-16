package com.cobelpvp.atheneum.deathmessage.damage;

import java.util.UUID;
import org.bukkit.entity.EntityType;

public abstract class MobDamage extends Damage
{
    private final EntityType mobType;

    public MobDamage(final UUID damaged, final double damage, final EntityType mobType) {
        super(damaged, damage);
        this.mobType = mobType;
    }

    public EntityType getMobType() {
        return this.mobType;
    }
}
