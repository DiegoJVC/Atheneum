package com.cobelpvp.atheneum.nametag;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public abstract class NametagProvider {

    @Getter
    private final String name;
    @Getter
    private final int weight;

    @ConstructorProperties({"name", "weight"})
    public NametagProvider(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public static final NametagInfo createNametag(String prefix, String suffix) {
        return TeamsNametagHandler.getOrCreate(prefix, suffix);
    }

    public abstract NametagInfo fetchNametag(Player var1, Player var2);

    public static final class DefaultNametagProvider extends NametagProvider {
        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return createNametag("", "");
        }
    }
}
