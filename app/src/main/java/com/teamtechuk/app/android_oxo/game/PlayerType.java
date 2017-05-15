package com.teamtechuk.app.android_oxo.game;

/**
 * Created by jimdixon on 27/03/2017.
 */

public enum PlayerType {
    ROCK(0),
    PAPER(1),
    SCISSORS(2);

    private final int id;

    PlayerType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
