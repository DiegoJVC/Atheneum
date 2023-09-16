package com.cobelpvp.atheneum.menu.menus;

import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.atheneum.util.Callback;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.buttons.BooleanButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {
    private final String title;
    private final Callback<Boolean> response;

    @ConstructorProperties({"title", "response"})
    public ConfirmMenu(final String title, final Callback<Boolean> response) {
        this.title = title;
        this.response = response;
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
            }
        }
        return buttons;
    }

    @Override
    public String getTitle(final Player player) {
        return this.title;
    }
}
