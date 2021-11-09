package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGatt;

public class BleGattClientReadRemoteRssiEventArgs {
    public final BluetoothGatt gatt;
    public final int rssi;
    public final int status;

    public BleGattClientReadRemoteRssiEventArgs(BluetoothGatt gatt, int rssi, int status) {
        this.gatt = gatt;
        this.rssi = rssi;
        this.status = status;
    }
}
