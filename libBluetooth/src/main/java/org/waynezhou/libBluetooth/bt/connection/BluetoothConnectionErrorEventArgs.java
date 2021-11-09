package org.waynezhou.libBluetooth.bt.connection;

import android.bluetooth.BluetoothDevice;

public class BluetoothConnectionErrorEventArgs {
    public final BluetoothDevice device;
    public final Exception e;
    public BluetoothConnectionErrorEventArgs(BluetoothDevice device, Exception e) {
        this.e = e;
        this.device = device;
    }
}
