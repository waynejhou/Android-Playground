package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothDevice;

public class BluetoothConnectionConnectedEventArgs {
    public final BluetoothDevice device;
    public BluetoothConnectionConnectedEventArgs(BluetoothDevice device) {
        this.device = device;
    }
}
