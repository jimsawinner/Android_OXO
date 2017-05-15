package com.teamtechuk.app.android_oxo.game;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class Board {
    public final static int MAX = 9;
    private PlayerType[] squares  = new PlayerType[MAX];

    public Board(){
        for(int i=0;i<MAX;i++)
            squares[i] = PlayerType.FREE;
    }

    public void reset(){
        for(int i=0;i<MAX;i++)
            squares[i] = PlayerType.FREE;
    }

    public void move(PlayerType player, int move){
        squares[move] = player;
    }

    public PlayerType checkWin(){
        //top row
        if((squares[0] == squares[1]) && (squares[1] == squares[2])){return squares[0];}
        //middle row
        else if((squares[3] == squares[4]) && (squares[4] == squares[5])){return squares[3];}
        //bottom row
        else if((squares[6] == squares[7]) && (squares[7] == squares[8])){return squares[6];}
        //first col
        else if((squares[0] == squares[3]) && (squares[3] == squares[6])){return squares[0];}
        //second col
        else if((squares[1] == squares[4]) && (squares[4] == squares[7])){return squares[1];}
        //third col
        else if((squares[2] == squares[5]) && (squares[5] == squares[8])){return squares[2];}
        //L2R diag
        else if((squares[0] == squares[4]) && (squares[4] == squares[8])){return squares[0];}
        //R2L diag
        else if((squares[2] == squares[4]) && (squares[4] == squares[6])){return squares[2];}
        return PlayerType.FREE;
    }

    public boolean[] getEmptySquares(){
        boolean[] emptySquares = new boolean[squares.length];
        for(int i=0;i<squares.length;i++){
            if(squares[i] == PlayerType.FREE){
                emptySquares[i] = true;
            }
            else
                emptySquares[i] = false;
        }
        return emptySquares;
    }
}