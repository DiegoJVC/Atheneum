package com.cobelpvp.atheneum.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.beans.ConstructorProperties;

public class HourEvent extends Event {
    private static final HandlerList handlerList;

    static {
        handlerList = new HandlerList();
    }

    private final int hour;

    @ConstructorProperties({"hour"})
    public HourEvent(final int hour) {
        this.hour = hour;
    }

    public static HandlerList getHandlerList() {
        return HourEvent.handlerList;
    }

    public HandlerList getHandlers() {
        return HourEvent.handlerList;
    }

    public int getHour() {
        return this.hour;
    }
}
