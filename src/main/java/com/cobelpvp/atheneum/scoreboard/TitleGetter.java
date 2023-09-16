package com.cobelpvp.atheneum.scoreboard;

import org.bukkit.entity.Player;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;

public class TitleGetter {

    private String defaultTitle;

    public TitleGetter(String defaultTitle) {
        this.defaultTitle = ChatColor.translateAlternateColorCodes('&', defaultTitle);
    }

    public TitleGetter() {
    }

    public static TitleGetter forStaticString(final String staticString) {
        Preconditions.checkNotNull(staticString);
        return new TitleGetter() {
            @Override
            public String getTitle(final Player player) {
                return staticString;
            }
        };
    }

    public String getTitle(final Player player) {
        return this.defaultTitle;
    }
}
