package com.teamtechuk.app.android_oxo.game;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class GameState {
    private HashMap<PlayerType,PlayerState> players;
    private  PlayerType currentPlayer;
    private  PlayerType opponentPlayer;
    private  Board board;
    private  String myNameTmp="";
    private  String opponentNameTmp="";
    private  PlayerType isThereAWinner = PlayerType.NO_WINNER;
    private static GameState GAMESTATE;

    private GameState(){
        players = new HashMap<>();
        players.put(PlayerType.NOUGHT,new PlayerState());
        players.put(PlayerType.CROSS, new PlayerState());
        board = new Board();
    }

    public static GameState getThisGame(){
        if(GAMESTATE == null){
            GAMESTATE = new GameState();
        }
        return GAMESTATE;
    }

    public void newGame(){
        board.reset();
        isThereAWinner = PlayerType.NO_WINNER;
    }

    public void setPlayerDetail(PlayerType me){
        currentPlayer = me;
        players.get(currentPlayer).setName(myNameTmp);
        players.get(currentPlayer).setPlayerType(currentPlayer);
        if(currentPlayer == PlayerType.CROSS){
            opponentPlayer = PlayerType.NOUGHT;
            players.get(opponentPlayer).setName(opponentNameTmp);
            players.get(opponentPlayer).setPlayerType(opponentPlayer);
        }
        else{
            players.get(PlayerType.CROSS).setName(opponentNameTmp);
            players.get(PlayerType.CROSS).setPlayerType(PlayerType.CROSS);
        }
    }

    public boolean processPlayerMove(PlayerMove move){
        if(opponentNameTmp.equals("")){
            Log.d("TAG", "processPlayerMove: ");
            if(!move.getPlayerName().equals(myNameTmp))
                opponentNameTmp = move.getPlayerName();
        }
        board.move(move.getPlayer(),move.getMove());
        PlayerType winner =  board.checkWin();
        if(winner != PlayerType.FREE){
            isThereAWinner = board.checkWin();
            players.get(move.getPlayer()).updateScore(board.checkWin());
            return true;
        }
        return false;
    }

    public boolean[] getEmptySquares(){
        return board.getEmptySquares();
    }

    public PlayerType checkForWinner(){
        return isThereAWinner;
    }

    public void setMyName(String name){
        this.myNameTmp=name;
    }

    public void setOponnentName(String name){
        this.opponentNameTmp=name;
    }

    public String getMyName(){
        return myNameTmp;
    }

    public String getOponnentName(){
        return opponentNameTmp;
    }
}