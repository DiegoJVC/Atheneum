package com.cobelpvp.atheneum.scoreboard;

import com.cobelpvp.atheneum.util.LinkedList;
import org.bukkit.entity.Player;

public interface ScoreGetter
{
    void getScores(final LinkedList<String> p0, final Player p1);
}
