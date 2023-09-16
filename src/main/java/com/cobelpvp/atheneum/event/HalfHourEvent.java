package com.cobelpvp.atheneum.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.beans.ConstructorProperties;

public class HalfHourEvent extends Event {
    private static final HandlerList handlerList;

    static {
        handlerList = new HandlerList();
    }

    private final int hour;
    private final int minute;

    @ConstructorProperties({"hour", "minute"})
    public HalfHourEvent(final int hour, final int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public static HandlerList getHandlerList() {
        return HalfHourEvent.handlerList;
    }

    public HandlerList getHandlers() {
        return HalfHourEvent.handlerList;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }
}
