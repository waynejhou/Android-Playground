package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGatt;

public class BleGattClientConnectionStateChangeEventArgs {
    public final BluetoothGatt gatt;
    public final int status;
    public final int newState;

    public BleGattClientConnectionStateChangeEventArgs(BluetoothGatt gatt, int status, int newState) {
        this.gatt = gatt;
        this.status = status;
        this.newState = newState;
    }
}
