package com.teamtechuk.app.android_oxo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.teamtechuk.app.android_oxo.fragments.DeviceDetailFragment;

/**
 * Created by jimdixon on 15/05/2017.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiDirectActivity activity;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiDirectActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent){

        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if WiFi P2P mode is enabled or not, alert the activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
            }else{
                activity.setIsWifiP2pEnabled(false);
            }
        }else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, (WifiP2pManager.PeerListListener)activity.getFragmentManager().findFragmentById(R.id.frag_list));
            }

            Log.d(WifiDirectActivity.TAG, "P2P Peers Changed");
        }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection state changed - we should probably do something
            if (manager == null) {
                log("Network manager appears to be null");
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP

                DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(R.id.frag_detail);

                manager.requestConnectionInfo(channel,fragment);
            }
        }else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // We'll update our display fragments here once we have created them.
        }
    }

    private void log(String msg){
        Log.d(this.getClass().toString(),msg);
    }
}