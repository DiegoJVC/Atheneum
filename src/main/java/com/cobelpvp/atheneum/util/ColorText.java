package com.cobelpvp.atheneum.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorText {

    public static String translate(String translate) {
        return ChatColor.translateAlternateColorCodes('&', translate);
    }

    public static List<String> translate(List<String> messages) {
        List<String> toReturn = new ArrayList<>();
        for (String message : messages) {
            toReturn.add(translate(message));
        }
        return toReturn;
    }
}
