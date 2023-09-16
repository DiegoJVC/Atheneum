package com.cobelpvp.atheneum.protocol.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class ServerLaggedOutEvent extends Event
{
    private static HandlerList handlerList = new HandlerList();
    private int averagePing;

    public ServerLaggedOutEvent(int averagePing) {
        super(true);
        this.averagePing = averagePing;
    }

    public HandlerList getHandlers() {
        return ServerLaggedOutEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return ServerLaggedOutEvent.handlerList;
    }
}
