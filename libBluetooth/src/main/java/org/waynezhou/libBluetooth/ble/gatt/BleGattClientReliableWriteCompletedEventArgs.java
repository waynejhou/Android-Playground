package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGatt;

public class BleGattClientReliableWriteCompletedEventArgs {
    public final BluetoothGatt gatt;
    public final int status;

    public BleGattClientReliableWriteCompletedEventArgs(BluetoothGatt gatt, int status) {
        this.gatt = gatt;
        this.status = status;
    }
}
