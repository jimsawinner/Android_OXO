package com.teamtechuk.app.android_oxo.game;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class PlayerMove {
    private PlayerType player;
    private int moveDetail;

    public PlayerMove(PlayerType player) {
        this.player = player;
    }

    public PlayerType getPlayerType() {
        return player;
    }

    public int getMoveDetail() {
        return moveDetail;
    }
}
