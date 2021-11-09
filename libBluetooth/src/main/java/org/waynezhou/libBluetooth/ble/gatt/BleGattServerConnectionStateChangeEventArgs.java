package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerConnectionStateChangeEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int status;
    public final int newState;

    public BleGattServerConnectionStateChangeEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int status, int newState) {
        this.gattServer = gattServer;
        this.device = device;
        this.status = status;
        this.newState = newState;
    }
}
