package com.cobelpvp.atheneum.menu.pagination;

import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.atheneum.menu.buttons.BackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {
    public static Menu lastMenu;
    private int page;

    public PaginatedMenu() {
        this.page = 1;
    }

    @Override
    public String getTitle(final Player player) {
        return this.getPrePaginatedTitle(player) + " - " + this.page + "/" + this.getPages(player);
    }

    public final void modPage(final Player player, final int mod) {
        this.page += mod;
        this.getButtons().clear();
        this.openMenu(player);
    }

    public final int getPages(final Player player) {
        final int buttonAmount = this.getAllPagesButtons(player).size();
        if (buttonAmount == 0) {
            return 1;
        }
        return (int) Math.ceil(buttonAmount / (double) this.getMaxItemsPerPage(player));
    }

    @Override
    public final Map<Integer, Button> getButtons(final Player player) {
        int minIndex = (int) ((this.page - 1) * (double) this.getMaxItemsPerPage(player));
        int maxIndex = (int) (this.page * (double) this.getMaxItemsPerPage(player));

        HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();

        for (int i = 1; i < 27; i++) {
            buttons.put(i, getPlaceholderButton());
        }

        buttons.put(18, new BackButton(lastMenu));

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        for (final Map.Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();
            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) (this.getMaxItemsPerPage(player) * (double) (this.page - 1)) - 10;
                buttons.put(ind, entry.getValue());
            }
        }

        Map<Integer, Button> global = this.getGlobalButtons(player);

        if (global != null) {
            for (final Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }
        }
        return buttons;
    }

    public int getMaxItemsPerPage(final Player player) {
        return 7;
    }

    public Map<Integer, Button> getGlobalButtons(final Player player) {
        return null;
    }

    public abstract String getPrePaginatedTitle(final Player p0);

    public abstract Map<Integer, Button> getAllPagesButtons(final Player p0);

    public int getPage() {
        return this.page;
    }
}
