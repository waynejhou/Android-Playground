package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerExecuteWriteEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int requestId;
    public final boolean execute;

    public BleGattServerExecuteWriteEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int requestId, boolean execute) {
        this.gattServer = gattServer;
        this.device = device;
        this.requestId = requestId;
        this.execute = execute;
    }
}
