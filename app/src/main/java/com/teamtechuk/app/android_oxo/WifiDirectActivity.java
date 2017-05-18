package com.teamtechuk.app.android_oxo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.teamtechuk.app.android_oxo.fragments.DeviceDetailFragment;
import com.teamtechuk.app.android_oxo.fragments.DeviceListFragment;

/**
 * Created by jimdixon on 15/05/2017.
 */

public class WifiDirectActivity extends Activity implements WifiP2pManager.ChannelListener, DeviceActionListener {

    public static final String TAG = "WifiDirectActivity";

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    private BroadcastReceiver receiver = null;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private WifiP2pConfig opponent;
    Handler mUpdateHandler;

    // Override configureDetails method from DeviceActionListener interface
    @Override
    public void configureDetails(WifiP2pDevice device) {
        opponent = configure(device);
        connect(opponent);
    }

    private WifiP2pConfig configure(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        log("Device Address: "+device.deviceAddress);
        config.wps.setup = WpsInfo.PBC;
        return config;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_frag);
        // add necessary intent values to be matched.
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
                if(manager !=null && channel !=null) {
                    // Since this is the system wireless setting activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }else{
                    Log.d("TAG", "channel or manager is null");
                }
                return true;

            case R.id.atn_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WifiDirectActivity.this, "Enable P2P from action bar button above or system settings", Toast.LENGTH_SHORT).show();
                    return true;
                }

                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiDirectActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiDirectActivity.this, "Discovery Failed : " + reasonCode, Toast.LENGTH_SHORT).show();
                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    @Override
    public void cancelDisconnect() {
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);

            if (fragment.getDevice() == null || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                manager.cancelConnect(channel, new WifiP2pManager.ActionListener(){

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiDirectActivity.this, "Aborting Connection", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        log("Connect abort request failed. Reason Code: "+reason);
                    }
                });
            }
        }
    }

    @Override
    public void connect(WifiP2pConfig config) {
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
//        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();

//        ft.addToBackStack(null);
//        ft.setCustomAnimations(R.anim.exit_to_left);

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // nothing to to here
                Toast.makeText(WifiDirectActivity.this, "Attempting to Connect", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirectActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int reason) {
                fragment.getView().setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
        // channel disconnected so we will try a re-connect
        if (manager !=null && !retryChannel) {
            Toast.makeText(this, "Channel Lost. Trying Again", Toast.LENGTH_SHORT).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this, "Severe! Channle is lost. Try Disable/Enable P2P",Toast.LENGTH_SHORT).show();
        }
    }

    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

        if(fragmentList != null) {
            fragmentList.clearPeers();
        }
    }

    public Handler getHandler(){
        return mUpdateHandler;
    }

    private void log(String msg){
        Log.d(this.getClass().toString(),msg);
    }
}
