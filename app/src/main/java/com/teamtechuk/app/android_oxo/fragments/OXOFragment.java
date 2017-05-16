package com.teamtechuk.app.android_oxo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.teamtechuk.app.android_oxo.R;
import com.teamtechuk.app.android_oxo.game.Board;
import com.teamtechuk.app.android_oxo.game.GameState;
import com.teamtechuk.app.android_oxo.game.PlayerMove;
import com.teamtechuk.app.android_oxo.game.PlayerState;
import com.teamtechuk.app.android_oxo.game.PlayerType;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class OXOFragment extends Fragment {
    private View mContentView = null;
    private  boolean isServer;
    private DeviceDetailFragment deviceDetailFragment;
    private static OXOFragment oxoFragment;
    private Gson gson;
    private PlayerState me;
    private static int[] playerImages = {R.drawable.x,R.drawable.o};
    private static int[] boardImages = {R.drawable.me,R.drawable.user};

    private static int[] squares = {
            R.id.sq_1, R.id.sq_2, R.id.sq_3,
            R.id.sq_4, R.id.sq_5, R.id.sq_6,
            R.id.sq_7, R.id.sq_8, R.id.sq_9
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.content_main, null);
        gson = new Gson();
        setupBtnClicks();
        oxoFragment = this;
        GameState.getThisGame();
        return mContentView;
    }

    private void setupBtnClicks() {
        for(int i=0; i<squares.length;i++) {
            mContentView.findViewById(squares[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "Button has been clicked");
                    deviceDetailFragment = (DeviceDetailFragment)getFragmentManager().findFragmentById(R.id.frag_detail);
                    ImageButton iBtn = (ImageButton) v;

                    //first time set player detail
                    if(me==null) {
                        me = new PlayerState(GameState.getThisGame().getMyName());
                        if (DeviceDetailFragment.isServer()) {
                            isServer=true;
                            me.setName("Server");
                            me.setPlayerType(PlayerType.NOUGHT);
                            enableInterface(false);
                        }
                        else {
                            isServer=false;
                            me.setName("Client");
                            me.setPlayerType(PlayerType.CROSS);
                            enableInterface(true);
                        }
                        GameState.getThisGame().setPlayerDetail(me.getPlayerType());
                    }
                    iBtn.setImageResource(playerImages[me.getPlayerType().getValue()]);
                    iBtn.setOnClickListener(null);
                    enableInterface(false);
                    for (int i = 0; i < Board.MAX; i++) {
                        if (squares[i] == iBtn.getId()) {
                            PlayerMove pMove = new PlayerMove(me.getPlayerType(), i);
                            GameState.getThisGame().processPlayerMove(pMove);
                            if(GameState.getThisGame().checkForWinner() != PlayerType.NO_WINNER)
                                gameOverRoutine();
                            if(isServer)
                                deviceDetailFragment.sendServerMove(gson.toJson(pMove));
                            else
                                deviceDetailFragment.sendClientMove(gson.toJson(pMove));
                        }
                    }
                }
            });
        }
    }

    public static void handleOpponentMove(PlayerMove playerMove){
        Log.d("TAG", "Opponent move detected");
        Activity activity = oxoFragment.getActivity();
        View view = oxoFragment.mContentView;
        final ImageButton btn = (ImageButton) view.findViewById(squares[playerMove.getMove()]);
        final PlayerMove pMove = playerMove;
        GameState.getThisGame().processPlayerMove(pMove);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setImageResource(playerImages[pMove.getPlayer().getValue()]);
                btn.setOnClickListener(null);
                Log.d("TAG", "Check for Winner");
                if(GameState.getThisGame().checkForWinner() != PlayerType.NO_WINNER) {
                    Log.d("TAG", "Game over detected");
                    gameOverRoutine();
                }else {
                    Log.d("TAG", "Enable my interface!!!");
                    enableInterface(true);
                }

            }
        });
    }

    public static void enableInterface(boolean set){
        boolean[] enabledSquares = GameState.getThisGame().getEmptySquares();
        for(int i=0;i<squares.length;i++)
            if(enabledSquares[i]){
                ImageButton iBtn = (ImageButton) oxoFragment.mContentView.findViewById(squares[i]);
                iBtn.setEnabled(set);
            }
    }

    public static void gameOverRoutine() {
        Log.d("TAG", "GAME OVER ROUTINE HIT");
        GameState.getThisGame().newGame();
        resetBoard();
        enableInterface(!oxoFragment.isServer);
    }

    private static void resetBoard(){
        for(int i=0;i<squares.length;i++){
            ImageButton iBtn = (ImageButton) oxoFragment.mContentView.findViewById(squares[i]);
            iBtn.setImageResource(android.R.color.transparent);
        }
    }

    private void setPlayerNames(){

    }

    private void log(String msg){
        Log.d(this.getClass().toString(),msg);
    }
}