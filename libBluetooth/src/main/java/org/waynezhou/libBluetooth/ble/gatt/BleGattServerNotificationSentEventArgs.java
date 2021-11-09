package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerNotificationSentEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int status;

    public BleGattServerNotificationSentEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int status) {
        this.gattServer = gattServer;
        this.device = device;
        this.status = status;
    }
}
