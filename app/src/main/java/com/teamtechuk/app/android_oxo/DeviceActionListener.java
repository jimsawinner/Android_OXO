package com.teamtechuk.app.android_oxo;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by jimdixon on 15/05/2017.
 */

public interface DeviceActionListener {
    void configureDetails(WifiP2pDevice device);
    void cancelDisconnect();
    void connect(WifiP2pConfig config);
    void disconnect();
}