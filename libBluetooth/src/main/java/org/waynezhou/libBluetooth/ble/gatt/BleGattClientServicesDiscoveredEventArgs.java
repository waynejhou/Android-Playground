package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;

public class BleGattClientServicesDiscoveredEventArgs {
    public final BluetoothGatt gatt;
    public final int status;

    public BleGattClientServicesDiscoveredEventArgs(BluetoothGatt gatt, int status) {
        this.gatt = gatt;
        this.status = status;
    }
}
