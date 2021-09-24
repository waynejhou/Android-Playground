package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;

public class BleGattClientMtuChangedEventArgs {
    public final BluetoothGatt gatt;
    public final int mtu;
    public final int status;

    public BleGattClientMtuChangedEventArgs(BluetoothGatt gatt, int mtu, int status) {
        this.gatt = gatt;
        this.mtu = mtu;
        this.status = status;
    }
}
