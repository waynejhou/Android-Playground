package org.waynezhou.libBluetooth.bt.discover;

import android.bluetooth.BluetoothAdapter;

public class BtDiscoverHandler {
    private final BluetoothAdapter adapter;
    protected BtDiscoverHandler(BluetoothAdapter adapter){
        this.adapter = adapter;
    }
    public void stop(){
        adapter.cancelDiscovery();
    }
}
