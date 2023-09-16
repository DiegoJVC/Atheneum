package com.cobelpvp.atheneum.util;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_7_R4.NBTBase;
import net.minecraft.server.v1_7_R4.NBTTagString;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ExecutableNBTTag extends NBTTagString {
    private final Callback<Player> callback;

    public void execute(Player player) {
        callback.callback(player);
    }

    @Override
    public NBTBase clone() {
        return new ExecutableNBTTag(callback);
    }
}