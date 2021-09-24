package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothDevice;

public class BluetoothConnectionAcceptedEventArgs {
    private final BluetoothDevice device;

    public BluetoothConnectionAcceptedEventArgs(BluetoothDevice device) {
        this.device = device;
    }
}

