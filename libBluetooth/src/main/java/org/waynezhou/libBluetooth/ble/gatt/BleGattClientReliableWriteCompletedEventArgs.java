package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;

public class BleGattClientReliableWriteCompletedEventArgs {
    public final BluetoothGatt gatt;
    public final int status;

    public BleGattClientReliableWriteCompletedEventArgs(BluetoothGatt gatt, int status) {
        this.gatt = gatt;
        this.status = status;
    }
}
