package org.waynezhou.libBluetooth.bt.connection;

import android.bluetooth.BluetoothDevice;

public class BluetoothConnectionConnectedEventArgs {
    public final BluetoothDevice device;
    public BluetoothConnectionConnectedEventArgs(BluetoothDevice device) {
        this.device = device;
    }
}
