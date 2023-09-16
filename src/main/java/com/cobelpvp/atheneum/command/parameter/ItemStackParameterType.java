package com.cobelpvp.atheneum.command.parameter;

import com.cobelpvp.atheneum.command.ParameterType;
import com.cobelpvp.atheneum.util.ItemUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class ItemStackParameterType implements ParameterType<ItemStack> {
    @Override
    public ItemStack transform(final CommandSender sender, final String source) {
        final ItemStack item = ItemUtils.get(source);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "No item with the name " + source + " found.");
            return null;
        }
        return item;
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String prefix) {
        return ImmutableList.of();
    }
}
