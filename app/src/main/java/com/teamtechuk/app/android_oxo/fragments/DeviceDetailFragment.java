package com.teamtechuk.app.android_oxo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamtechuk.app.android_oxo.DeviceActionListener;
import com.teamtechuk.app.android_oxo.R;
import com.teamtechuk.app.android_oxo.game.PlayerMove;
import com.teamtechuk.app.android_oxo.game.PlayerType;
import com.teamtechuk.app.android_oxo.socket.DataSocketManager;
import com.teamtechuk.app.android_oxo.socket.SocketServerService;

/**
 * Created by jimdixon on 15/05/2017.
 */

public class DeviceDetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {

    private View mContentView = null;
    private WifiP2pInfo info;
    private ProgressDialog progressDialog = null;

    private String clientMessage;
    private String serverMessage;

    private static int port = 10051;

    Intent mServiceIntent;
    WifiP2pDevice device;

    private Boolean server=false;
    private Gson moveObject = new Gson();

    public static DeviceDetailFragment deviceDetailFragment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.setVisibility(View.INVISIBLE);

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DeviceActionListener) getActivity()).disconnect();
            }
        });

        // Handler for game buttons
        mContentView.findViewById(R.id.btn_rock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = (TextView) mContentView.findViewById(R.id.myMove);
                txt.setText("My Move: " + PlayerType.ROCK.toString());
                PlayerMove move = new PlayerMove(PlayerType.ROCK);
                if(server) {
                    sendServerMove(moveToJson(move));
                }else{
                    sendClientMove(moveToJson(move));
                }
            }
        });

        mContentView.findViewById(R.id.btn_paper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = (TextView) mContentView.findViewById(R.id.myMove);
                txt.setText("My Move: " + PlayerType.PAPER.toString());
                PlayerMove move = new PlayerMove(PlayerType.PAPER);
                if(server) {
                    sendServerMove(moveToJson(move));
                }else{
                    sendClientMove(moveToJson(move));
                }
            }
        });

        mContentView.findViewById(R.id.btn_scissors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = (TextView) mContentView.findViewById(R.id.myMove);
                txt.setText("My Move: " + PlayerType.SCISSORS.toString());
                PlayerMove move = new PlayerMove(PlayerType.SCISSORS);
                if(server) {
                    sendServerMove(moveToJson(move));
                }else{
                    sendClientMove(moveToJson(move));
                }
            }
        });

        deviceDetailFragment = this;
        return mContentView;
    }

    public void sendClientMove(String nextMove) {
        Intent serviceIntent = new Intent(getActivity(), DataSocketManager.class);
        serviceIntent.setAction(DataSocketManager.ACTION_SEND_MESSAGE);
        serviceIntent.putExtra(DataSocketManager.EXTRAS_MESSAGE, nextMove);
        serviceIntent.putExtra(DataSocketManager.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(DataSocketManager.EXTRAS_GROUP_OWNER_PORT, port);
        getActivity().startService(serviceIntent);
    }

    public void sendServerMove(String nextMove) {
        SocketServerService.setMessage(nextMove);
        SocketServerService.nextTurn();
    }

    private String moveToJson(PlayerMove move) {
        String s = moveObject.toJson(move);
        return s;
    }

    public static void setOpponentMove(PlayerMove move) {
        Activity activity = deviceDetailFragment.getActivity();
        FragmentManager fManager = activity.getFragmentManager();
        DeviceDetailFragment ddFragment = (DeviceDetailFragment) fManager.findFragmentById(R.id.frag_detail);

        final PlayerMove nextMove = move;
        final TextView txt = (TextView) ddFragment.mContentView.findViewById(R.id.opponentMove);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt.setText("Opponent Move: "+nextMove.getPlayerType().toString());
            }
        });

    }

//    public void sendClientMessage() {
//        log("Prepare client message...");
//        Calendar rightNow = Calendar.getInstance();
//        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
//        clientMessage = "Client Time: "+Long.toString((rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000));
//        Intent serviceIntent = new Intent(getActivity(), DataSocketManager.class);
//        serviceIntent.setAction(DataSocketManager.ACTION_SEND_MESSAGE);
//        serviceIntent.putExtra(DataSocketManager.EXTRAS_MESSAGE, clientMessage);
//        serviceIntent.putExtra(DataSocketManager.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
//        serviceIntent.putExtra(DataSocketManager.EXTRAS_GROUP_OWNER_PORT, port);
//        getActivity().startService(serviceIntent);
//    }

//    public void sendServerMessage(String msg) {
//        log("Prepare server message...");
//        Calendar rightNow = Calendar.getInstance();
//        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
//        serverMessage = "Server Time: "+Long.toString((rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000));
//        SocketServerService.setMessage(serverMessage);

    // Call nextTurn on the SocketServerService
//        SocketServerService.nextTurn();
//    }

    public void startSocketServerService(){
        Intent serviceIntent = new Intent(getActivity(), SocketServerService.class);
        serviceIntent.setAction(SocketServerService.ACTION_START_SERVER);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        log("IN onConnectionInfoAvailable...");
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        this.info = info;

        // Show the connection info box (red box)
        this.getView().setVisibility(View.VISIBLE);

        // The owners IP is now known
//        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);

        // Set the group_owner_text textview value
//        view.setText(getResources().getString(R.string.group_owner_text) + " - " + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes) : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
//        view = (TextView) mContentView.findViewById(R.id.group_ip);
//        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if(info.groupFormed && info.isGroupOwner) {
            // A group has been formed and we are the group owner
//            mContentView.findViewById(R.id.server_send_msg).setVisibility(View.VISIBLE);

            // start socket server service
            log("We are the group owner - start a socket server service");
            server=true;
            startSocketServerService();
        } else if (info.groupFormed && !info.isGroupOwner) {
            // A group has been formed - but we are not the group owner
            log("We are NOT group owner.");
            server=false;
//            mContentView.findViewById(R.id.client_send_msg).setVisibility(View.VISIBLE);
//            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.client_text));
        }


    }

    public void showDetails(WifiP2pDevice device) {

    }

    public void resetViews() {

    }

    private void log(String msg){
        Log.d(this.getClass().toString(), msg);
    }
}
