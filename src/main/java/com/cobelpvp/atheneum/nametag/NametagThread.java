package com.cobelpvp.atheneum.nametag;

import lombok.Getter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagThread extends Thread {

    @Getter
    private static final Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap();
    private TeamsNametagHandler nametags;

    public NametagThread() {
        super("Atheneum - Nametag Thread");
        this.setDaemon(true);
    }

    public void run() {
        while (true) {
            Iterator pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while (pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = (NametagUpdate) pendingUpdatesIterator.next();

                try {
                    TeamsNametagHandler.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }

            try {
                Thread.sleep((long) TeamsNametagHandler.getUpdateInterval() * 50L);
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }
        }
    }
}
