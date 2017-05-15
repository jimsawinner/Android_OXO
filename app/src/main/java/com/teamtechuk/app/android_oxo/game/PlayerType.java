package com.teamtechuk.app.android_oxo.game;

/**
 * Created by jimdixon on 27/03/2017.
 */

public enum PlayerType {
    CROSS(0),
    NOUGHT(1),
    FREE(2),
    NO_WINNER(3);

    private final int id;

    PlayerType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

}