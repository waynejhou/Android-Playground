package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;

public class BleGattServerServiceAddedEventArgs {
    public final BluetoothGattServer gattServer;
    public final int status;
    public final BluetoothGattService service;

    public BleGattServerServiceAddedEventArgs(BluetoothGattServer gattServer, int status, BluetoothGattService service) {
        this.gattServer = gattServer;
        this.status = status;
        this.service = service;
    }
}
