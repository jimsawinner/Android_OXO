package com.teamtechuk.app.android_oxo.socket;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.teamtechuk.app.android_oxo.fragments.DeviceDetailFragment;
import com.teamtechuk.app.android_oxo.game.PlayerMove;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class DataSocketManager extends IntentService {
    private String TAG = "DataSocketManager";
    public static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String EXTRAS_MESSAGE = "MESSAGE";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "HOST";
    public static final String EXTRAS_GROUP_OWNER_PORT = "PORT";

    private String message;
    private Context context;
    int port;
    String hostAddress;
    static Boolean myTurn = false;
    boolean listen=false;
    Gson moveObject = new Gson();

    public DataSocketManager(String name) { super(name); }
    public DataSocketManager() { super("DataSocketManager"); }

    public static void nextTurn() {
        myTurn = !myTurn;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_MESSAGE)) {
            nextTurn();
            message = intent.getExtras().getString(EXTRAS_MESSAGE);
            port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            hostAddress = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        }

        // Ensure NOT to hold reference to thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (
                        Socket clientSocket = new Socket(hostAddress, port);
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), false);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
                    if(!listen){
                        while(!myTurn){;}
                        out.print(message + '\n');
                        out.flush();
                        listen=true;
                        myTurn=false;
                    }

                    while (listen){
                        String fromServer = in.readLine();
                        if(fromServer != null && !fromServer.isEmpty()) {
                            PlayerMove move = moveObject.fromJson(fromServer, PlayerMove.class);
                            DeviceDetailFragment.setOpponentMove(move);
                            log(fromServer);
                            listen = false;
                            break;
                        }
                    }
                }catch (UnknownHostException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
