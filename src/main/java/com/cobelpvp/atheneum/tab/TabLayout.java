package com.cobelpvp.atheneum.tab;

import com.cobelpvp.atheneum.Atheneum;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TabLayout {
    protected static final String EMPTY_TAB_HEADERFOOTER = "{\"translate\":\"\"}";
    private static final AtomicReference<Object> TAB_LAYOUT_1_8;
    private static final AtomicReference<Object> TAB_LAYOUT_DEFAULT;
    private static final String[] ZERO_VALUE_STRING;
    private static final String[] ZERO_VALUE_STRING_18;
    private static final Map<String, TabLayout> tabLayouts;
    protected static int WIDTH;
    protected static int HEIGHT;
    private static List<String> emptyStrings;

    static {
        TAB_LAYOUT_1_8 = new AtomicReference<Object>();
        TAB_LAYOUT_DEFAULT = new AtomicReference<Object>();
        ZERO_VALUE_STRING = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        ZERO_VALUE_STRING_18 = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        tabLayouts = new HashMap<String, TabLayout>();
        TabLayout.WIDTH = 3;
        TabLayout.HEIGHT = 20;
        TabLayout.emptyStrings = new ArrayList<String>();
    }

    private final boolean is18;
    private String[] tabNames;
    private int[] tabPings;
    private String header;
    private String footer;

    private TabLayout(final boolean is18) {
        this(is18, false);
    }

    private TabLayout(final boolean is18, final boolean fill) {
        this.header = "{\"translate\":\"\"}";
        this.footer = "{\"translate\":\"\"}";
        this.is18 = is18;
        this.tabNames = (is18 ? TabLayout.ZERO_VALUE_STRING_18.clone() : TabLayout.ZERO_VALUE_STRING.clone());
        this.tabPings = (is18 ? new int[TabLayout.WIDTH * TabLayout.HEIGHT + 20] : new int[TabLayout.WIDTH * TabLayout.HEIGHT]);
        if (fill) {
            for (int i = 0; i < this.tabNames.length; ++i) {
                this.tabNames[i] = genEmpty();
                this.tabPings[i] = 0;
            }
        }
        Arrays.sort(this.tabNames);
    }

    private static String genEmpty() {
        final String colorChars = "abcdefghijpqrstuvwxyz0123456789";
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; ++i) {
            builder.append('§').append(colorChars.charAt(Atheneum.RANDOM.nextInt(colorChars.length())));
        }
        final String s = builder.toString();
        if (TabLayout.emptyStrings.contains(s)) {
            return genEmpty();
        }
        TabLayout.emptyStrings.add(s);
        return s;
    }

    public static TabLayout create(final Player player) {
        if (TabLayout.tabLayouts.containsKey(player.getName())) {
            final TabLayout layout = TabLayout.tabLayouts.get(player.getName());
            layout.reset();
            return layout;
        }
        TabLayout.tabLayouts.put(player.getName(), new TabLayout(TabUtils.is18(player)));
        return TabLayout.tabLayouts.get(player.getName());
    }

    protected static void remove(final Player player) {
        TabLayout.tabLayouts.remove(player.getName());
    }

    public static TabLayout createEmpty(final Player player) {
        if (TabUtils.is18(player)) {
            return getTAB_LAYOUT_1_8();
        }
        return getTAB_LAYOUT_DEFAULT();
    }

    public static TabLayout getTAB_LAYOUT_1_8() {
        Object value = TabLayout.TAB_LAYOUT_1_8.get();
        if (value == null) {
            synchronized (TabLayout.TAB_LAYOUT_1_8) {
                value = TabLayout.TAB_LAYOUT_1_8.get();
                if (value == null) {
                    final TabLayout actualValue = new TabLayout(true, true);
                    value = ((actualValue == null) ? TabLayout.TAB_LAYOUT_1_8 : actualValue);
                    TabLayout.TAB_LAYOUT_1_8.set(value);
                }
            }
        }
        return (TabLayout) ((value == TabLayout.TAB_LAYOUT_1_8) ? null : value);
    }

    public static TabLayout getTAB_LAYOUT_DEFAULT() {
        Object value = TabLayout.TAB_LAYOUT_DEFAULT.get();
        if (value == null) {
            synchronized (TabLayout.TAB_LAYOUT_DEFAULT) {
                value = TabLayout.TAB_LAYOUT_DEFAULT.get();
                if (value == null) {
                    final TabLayout actualValue = new TabLayout(false, true);
                    value = ((actualValue == null) ? TabLayout.TAB_LAYOUT_DEFAULT : actualValue);
                    TabLayout.TAB_LAYOUT_DEFAULT.set(value);
                }
            }
        }
        return (TabLayout) ((value == TabLayout.TAB_LAYOUT_DEFAULT) ? null : value);
    }

    public void set(final int x, final int y, final String name, final int ping) {
        if (!this.validate(x, y, true)) {
            return;
        }
        final int pos = this.is18 ? (y + x * TabLayout.HEIGHT) : (x + y * TabLayout.WIDTH);
        this.tabNames[pos] = ChatColor.translateAlternateColorCodes('&', name);
        this.tabPings[pos] = ping;
    }

    public void set(final int x, final int y, final String name) {
        this.set(x, y, name, 0);
    }

    public void set(final int x, final int y, final Player player) {
        this.set(x, y, player.getName(), ((CraftPlayer) player).getHandle().ping);
    }

    public String getStringAt(final int x, final int y) {
        this.validate(x, y);
        final int pos = this.is18 ? (y + x * TabLayout.HEIGHT) : (x + y * TabLayout.WIDTH);
        return this.tabNames[pos];
    }

    public int getPingAt(final int x, final int y) {
        this.validate(x, y);
        final int pos = this.is18 ? (y + x * TabLayout.HEIGHT) : (x + y * TabLayout.WIDTH);
        return this.tabPings[pos];
    }

    public boolean validate(final int x, final int y, final boolean silent) {
        if (x >= TabLayout.WIDTH) {
            if (!silent) {
                throw new IllegalArgumentException("x >= WIDTH (" + TabLayout.WIDTH + ")");
            }
            return false;
        } else {
            if (y < TabLayout.HEIGHT) {
                return true;
            }
            if (!silent) {
                throw new IllegalArgumentException("y >= HEIGHT (" + TabLayout.HEIGHT + ")");
            }
            return false;
        }
    }

    public boolean validate(final int x, final int y) {
        return this.validate(x, y, false);
    }

    protected String[] getTabNames() {
        return this.tabNames;
    }

    protected int[] getTabPings() {
        return this.tabPings;
    }

    public boolean is18() {
        return this.is18;
    }

    public void reset() {
        this.tabNames = (this.is18 ? TabLayout.ZERO_VALUE_STRING_18.clone() : TabLayout.ZERO_VALUE_STRING.clone());
        this.tabPings = (this.is18 ? new int[TabLayout.WIDTH * TabLayout.HEIGHT + 20] : new int[TabLayout.WIDTH * TabLayout.HEIGHT]);
    }

    protected String getHeader() {
        return this.header;
    }

    public void setHeader(final String header) {
        this.header = ComponentSerializer.toString(new TextComponent(ChatColor.translateAlternateColorCodes('&', header)));
    }

    protected String getFooter() {
        return this.footer;
    }

    public void setFooter(final String footer) {
        this.footer = ComponentSerializer.toString(new TextComponent(ChatColor.translateAlternateColorCodes('&', footer)));
    }
}
