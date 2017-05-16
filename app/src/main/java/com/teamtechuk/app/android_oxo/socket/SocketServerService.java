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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jimdixon on 27/03/2017.
 */

public class SocketServerService extends IntentService {
    private static String TAG = "SocketServerService";
    public final static String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    public final static String EXTRAS_MESSAGE = "MESSAGE";
    public final static String ACTION_START_SERVER = "ACTION_START_SERVER";

    int port = 10051;
    static Boolean myTurn=false;
    Boolean listen=true;
    static String message;
    //    private String clientMessage;
    private String clientMove;
    private Gson moveObject = new Gson();

    public SocketServerService(String name) { super(name); }
    public SocketServerService() { super("SocketServerService"); }

    public static void nextTurn() {
        myTurn = !myTurn;
    }

    public static void setMessage(String msg) {
        message = msg;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_START_SERVER)) {
            // Important not to hold reference to thread
            new Thread(new Runnable() {
                // Some Error Checking Functionality
                Boolean error=false;

                public void run() {

                    while (true) {
                        if(error){
                            // an error occurred previously - dont continue to run up a thread
                            return;
                        }

                        try (
                                ServerSocket dataSocket = new ServerSocket(port);
                                Socket clientSocket = dataSocket.accept();
                                PrintWriter outputStreamWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());

                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        ) {
                            if (listen) { // Listening for a client message
                                log("listening for client message...");
                                clientMove = bufferedReader.readLine();
                                if (clientMove != null) {
                                    log(clientMove);
                                    PlayerMove move = moveObject.fromJson(clientMove, PlayerMove.class);
                                    DeviceDetailFragment.setOpponentMove(move);
//                                    log(clientMessage);
                                    listen = false;
                                }
                            }

                            if (!listen) {
                                log("ready to send message...");
                                while (!myTurn) {
                                    ;
                                }
                                outputStreamWriter.print(message + '\n');
                                outputStreamWriter.flush();
                                listen = true;
                                myTurn = false;
                            }
                        } catch (IOException e) {
                            log(e.getMessage());
                            error=true;
                        }
                    }
                }
            }).start();
        }
    }
    private void log(String msg){
        Log.d(TAG, msg);
    }
}