package com.cobelpvp.atheneum.hologram.construct;

import com.cobelpvp.atheneum.util.EntityUtils;

public class HologramLine {
    private final int skullId = EntityUtils.getFakeEntityId();
    private final int horseId = EntityUtils.getFakeEntityId();
    private String text;

    public HologramLine(String text) {
        this.text = text;
    }

    public int getSkullId() {
        return this.skullId;
    }

    public int getHorseId() {
        return this.horseId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
