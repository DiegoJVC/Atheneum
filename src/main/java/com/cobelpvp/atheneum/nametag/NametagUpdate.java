package com.cobelpvp.atheneum.nametag;

import lombok.Getter;
import org.bukkit.entity.Player;

final class NametagUpdate {

    @Getter
    private final String toRefresh;
    @Getter
    private String refreshFor;

    public NametagUpdate(Player toRefresh) {
        this.toRefresh = toRefresh.getName();
    }

    public NametagUpdate(Player toRefresh, Player refreshFor) {
        this.toRefresh = toRefresh.getName();
        this.refreshFor = refreshFor.getName();
    }
}
