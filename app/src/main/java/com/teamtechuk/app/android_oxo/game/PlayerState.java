package com.teamtechuk.app.android_oxo.game;

import java.util.ArrayList;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class PlayerState {
    private String name;
    private PlayerType playerType;
    private boolean myTurn=false;
    private ArrayList<PlayerType> gamesPlayed;

    public PlayerState(){
        gamesPlayed = new ArrayList<>();
    }

    public PlayerState(PlayerType playerType){
        gamesPlayed = new ArrayList<>();
        this.playerType = playerType;
    }

    public PlayerState(String name){
        gamesPlayed = new ArrayList<>();
        this.name = name;
    }

    public void setName(String n){
        name = n;
    }

    public String getName(){
        return name;
    }

    public void setPlayerType(PlayerType pt){
        playerType = pt;
    }

    public PlayerType getPlayerType(){
        return playerType;
    }

    public void updateScore(PlayerType winningPlayer){
        gamesPlayed.add(winningPlayer);
    }

    public boolean isItMyTurn(){return myTurn;}

    public void hadMyTurn(){myTurn = !myTurn;}
}
