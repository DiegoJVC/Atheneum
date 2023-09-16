package com.cobelpvp.atheneum.deathmessage;

import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import java.util.UUID;

public interface DeathMessageConfiguration
{
    public static final DeathMessageConfiguration DEFAULT_CONFIGURATION = new DeathMessageConfiguration() {
        @Override
        public boolean shouldShowDeathMessage(final UUID checkFor, final UUID died, final UUID killer) {
            return true;
        }

        @Override
        public String formatPlayerName(final UUID player) {
            return ChatColor.RED + UUIDUtils.name(player);
        }
    };

    boolean shouldShowDeathMessage(final UUID p0, final UUID p1, final UUID p2);

    String formatPlayerName(final UUID p0);

    default String formatPlayerName(final UUID player, final UUID formatFor) {
        return this.formatPlayerName(player);
    }

    default boolean hideWeapons() {
        return false;
    }
}
