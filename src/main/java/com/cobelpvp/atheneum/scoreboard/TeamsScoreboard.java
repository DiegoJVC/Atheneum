package com.cobelpvp.atheneum.scoreboard;

import com.cobelpvp.atheneum.packet.ScoreboardTeamPacketMod;
import com.cobelpvp.atheneum.util.LinkedList;
import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.server.v1_7_R4.Packet;
import com.google.common.collect.UnmodifiableIterator;

import com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;

import org.bukkit.scoreboard.Objective;
import org.bukkit.entity.Player;

public class TeamsScoreboard {

    private Player player;
    private Objective objective;
    private Map<String, Integer> displayedScores = new HashMap();
    private Map<String, String> scorePrefixes = new HashMap();
    private Map<String, String> scoreSuffixes = new HashMap();
    private Set<String> sentTeamCreates = new HashSet();
    private final StringBuilder separateScoreBuilder = new StringBuilder();
    private final List<String> separateScores = new ArrayList();
    private final Set<String> recentlyUpdatedScores = new HashSet();
    private final Set<String> usedBaseScores = new HashSet();
    private final String[] prefixScoreSuffix = new String[3];
    private final ThreadLocal<com.cobelpvp.atheneum.util.LinkedList<String>> localList = ThreadLocal.withInitial(LinkedList::new);

    public TeamsScoreboard(Player player) {
        this.player = player;
        Scoreboard board = Atheneum.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        this.objective = board.registerNewObjective("TeamsSQNetwork", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
    }

    public void update() {
        String untranslatedTitle = TeamsScoreboardHandler.getConfiguration().getTitleGetter().getTitle(this.player);
        String title = ChatColor.translateAlternateColorCodes('&', untranslatedTitle);
        List<String> lines = this.localList.get();
        if (!lines.isEmpty()) {
            lines.clear();
        }
        TeamsScoreboardHandler.getConfiguration().getScoreGetter().getScores(this.localList.get(), this.player);
        this.recentlyUpdatedScores.clear();
        this.usedBaseScores.clear();
        int nextValue = lines.size();
        Preconditions.checkArgument(lines.size() < 16, "Too many lines passed!");
        Preconditions.checkArgument(title.length() < 32, "Title is too long!");
        if (!this.objective.getDisplayName().equals(title)) {
            this.objective.setDisplayName(title);
        }
        String displayedScore;
        for(Iterator var5 = lines.iterator(); var5.hasNext(); --nextValue) {
            displayedScore = (String)var5.next();
            if (48 <= displayedScore.length()) {
                throw new IllegalArgumentException("Line is too long! Offending line: " + displayedScore);
            }
            String[] separated = this.separate(displayedScore, this.usedBaseScores);
            String prefix = separated[0];
            String score = separated[1];
            String suffix = separated[2];
            this.recentlyUpdatedScores.add(score);
            if (!this.sentTeamCreates.contains(score)) {
                this.createAndAddMember(score);
            }
            if (!this.displayedScores.containsKey(score) || (Integer)this.displayedScores.get(score) != nextValue) {
                this.setScore(score, nextValue);
            }

            if (!this.scorePrefixes.containsKey(score) || !((String)this.scorePrefixes.get(score)).equals(prefix) || !((String)this.scoreSuffixes.get(score)).equals(suffix)) {
                this.updateScore(score, prefix, suffix);
            }
        }
        UnmodifiableIterator var11 = ImmutableSet.copyOf(this.displayedScores.keySet()).iterator();
        while(var11.hasNext()) {
            displayedScore = (String)var11.next();
            if (!this.recentlyUpdatedScores.contains(displayedScore)) {
                this.removeScore(displayedScore);
            }
        }
    }

    private void setField(final Packet packet, final String field, final Object value) {
        try {
            final Field fieldObject = packet.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddMember(final String scoreTitle) {
        final ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", (Collection)ImmutableList.of(), 0);
        final ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, (Collection)ImmutableList.of((Object)scoreTitle), 3);
        scoreboardTeamAdd.sendToPlayer(this.player);
        scoreboardTeamAddMember.sendToPlayer(this.player);
        this.sentTeamCreates.add(scoreTitle);
    }

    private void setScore(final String score, final int value) {
        final PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();
        this.setField((Packet)scoreboardScorePacket, "a", score);
        this.setField((Packet)scoreboardScorePacket, "b", this.objective.getName());
        this.setField((Packet)scoreboardScorePacket, "c", value);
        this.setField((Packet)scoreboardScorePacket, "d", 0);
        this.displayedScores.put(score, value);
        ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket((Packet)scoreboardScorePacket);
    }

    private void removeScore(final String score) {
        this.displayedScores.remove(score);
        this.scorePrefixes.remove(score);
        this.scoreSuffixes.remove(score);
        ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(final String score, final String prefix, final String suffix) {
        this.scorePrefixes.put(score, prefix);
        this.scoreSuffixes.put(score, suffix);
        new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2).sendToPlayer(this.player);
    }

    private String[] separate(String line, final Collection<String> usedBaseScores) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        String prefix = "";
        String score = "";
        String suffix = "";
        this.separateScores.clear();
        this.separateScoreBuilder.setLength(0);
        for (int i = 0; i < line.length(); ++i) {
            final char c = line.charAt(i);
            if (c == '*' || (this.separateScoreBuilder.length() == 16 && this.separateScores.size() < 3)) {
                this.separateScores.add(this.separateScoreBuilder.toString());
                this.separateScoreBuilder.setLength(0);
                if (c == '*') {
                    continue;
                }
            }
            this.separateScoreBuilder.append(c);
        }
        this.separateScores.add(this.separateScoreBuilder.toString());
        switch (this.separateScores.size()) {
            case 1: {
                score = this.separateScores.get(0);
                break;
            }
            case 2: {
                score = this.separateScores.get(0);
                suffix = this.separateScores.get(1);
                break;
            }
            case 3: {
                prefix = this.separateScores.get(0);
                score = this.separateScores.get(1);
                suffix = this.separateScores.get(2);
                break;
            }
            default: {
                Atheneum.getInstance().getLogger().warning("Failed to separate scoreboard line. Input: " + line);
                break;
            }
        }
        if (usedBaseScores.contains(score)) {
            if (score.length() <= 14) {
                for (final ChatColor chatColor : ChatColor.values()) {
                    final String possibleScore = chatColor + score;
                    if (!usedBaseScores.contains(possibleScore)) {
                        score = possibleScore;
                        break;
                    }
                }
                if (usedBaseScores.contains(score)) {
                    Atheneum.getInstance().getLogger().warning("Failed to find alternate color code for: " + score);
                }
            }
            else {
                Atheneum.getInstance().getLogger().warning("Found a scoreboard base collision to shift: " + score);
            }
        }
        if (prefix.length() > 16) {
            prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }
        if (score.length() > 16) {
            score = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }
        if (suffix.length() > 16) {
            suffix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }
        usedBaseScores.add(score);
        this.prefixScoreSuffix[0] = prefix;
        this.prefixScoreSuffix[1] = score;
        this.prefixScoreSuffix[2] = suffix;
        return this.prefixScoreSuffix;
    }
}
