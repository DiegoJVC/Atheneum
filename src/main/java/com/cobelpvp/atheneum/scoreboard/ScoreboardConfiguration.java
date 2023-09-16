package com.cobelpvp.atheneum.scoreboard;

public final class ScoreboardConfiguration
{
    private TitleGetter titleGetter;
    private ScoreGetter scoreGetter;

    public TitleGetter getTitleGetter() {
        return this.titleGetter;
    }

    public void setTitleGetter(final TitleGetter titleGetter) {
        this.titleGetter = titleGetter;
    }

    public ScoreGetter getScoreGetter() {
        return this.scoreGetter;
    }

    public void setScoreGetter(final ScoreGetter scoreGetter) {
        this.scoreGetter = scoreGetter;
    }
}
