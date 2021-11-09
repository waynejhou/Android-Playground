package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothGattServer;

public class BleGattServerHandler {
    final BluetoothGattServer gattServer;

    public BleGattServerHandler(BluetoothGattServer gattServer) {
        this.gattServer = gattServer;
    }

    public void stop(){
        gattServer.close();
    }
}
