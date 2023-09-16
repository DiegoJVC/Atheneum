package com.cobelpvp.atheneum.combatlogger;

import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.deathmessage.TeamsDeathMessageHandler;
import org.bukkit.ChatColor;
import java.util.UUID;

public interface CombatLoggerConfiguration {

    public static final CombatLoggerConfiguration DEFAULT_CONFIGURATION = new CombatLoggerConfiguration() {
        @Override
        public String formatPlayerName(final UUID user) {
            if (TeamsDeathMessageHandler.getConfiguration() != null) {
                return TeamsDeathMessageHandler.getConfiguration().formatPlayerName(user) + ChatColor.GRAY + " (Combat-Logger)";
            }
            return ChatColor.RED + UUIDUtils.name(user) + ChatColor.GRAY + " (Combat-Logger)";
        }
    };

    String formatPlayerName(final UUID p0);

    default String formatPlayerName(final UUID user, final UUID formatFor) {
        return this.formatPlayerName(user);
    }
}
