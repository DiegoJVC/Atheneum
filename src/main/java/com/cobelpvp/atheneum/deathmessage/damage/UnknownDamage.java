package com.cobelpvp.atheneum.deathmessage.damage;

import org.bukkit.ChatColor;
import java.util.UUID;

public final class UnknownDamage extends Damage
{
    public UnknownDamage(final UUID damaged, final double damage) {
        super(damaged, damage);
    }

    @Override
    public String getDeathMessage(final UUID getFor) {
        return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " died.";
    }
}
