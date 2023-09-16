package com.cobelpvp.atheneum.nametag;

import com.cobelpvp.atheneum.packet.ScoreboardTeamPacketMod;
import lombok.Getter;

import java.util.ArrayList;

public final class NametagInfo {

    @Getter
    private final String name;
    @Getter
    private final String prefix;
    @Getter
    private final String suffix;
    @Getter
    private final ScoreboardTeamPacketMod teamAddPacket;

    protected NametagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList(), 0);
    }

    public boolean equals(Object other) {
        if (!(other instanceof NametagInfo)) {
            return false;
        } else {
            NametagInfo otherNametag = (NametagInfo) other;
            return this.name.equals(otherNametag.name) && this.prefix.equals(otherNametag.prefix) && this.suffix.equals(otherNametag.suffix);
        }
    }
}
