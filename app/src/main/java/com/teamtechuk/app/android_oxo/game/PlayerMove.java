package com.teamtechuk.app.android_oxo.game;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class PlayerMove {
    PlayerType player;
    String name;
    int move;

    public PlayerMove(PlayerType player, int move){
        this.player = player;
        this.move = move;
        this.name = GameState.getThisGame().getMyName();
    }

    public int getMove(){
        return move;
    }

    public PlayerType getPlayer(){
        return player;
    }

    public void setPlayerName(String name){
        this.name=name;
    }

    public String getPlayerName(){
        return name;
    }
}
