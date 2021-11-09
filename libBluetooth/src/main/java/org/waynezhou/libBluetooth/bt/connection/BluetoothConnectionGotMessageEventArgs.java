package org.waynezhou.libBluetooth.bt.connection;

import android.bluetooth.BluetoothDevice;

public class BluetoothConnectionGotMessageEventArgs {
    public final byte[] message;
    public final BluetoothDevice device;
    public BluetoothConnectionGotMessageEventArgs(BluetoothDevice device, byte[] message) {
        this.message = message;
        this.device = device;
    }
}
