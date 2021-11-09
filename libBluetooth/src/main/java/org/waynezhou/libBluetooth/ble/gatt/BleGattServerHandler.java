package org.waynezhou.libBluetooth.ble.gatt;

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
