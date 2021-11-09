package org.waynezhou.libBluetooth.bt.discover;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public class BluetoothDiscoverFoundEventArgs {
    public final BluetoothDevice device;

    public BluetoothDiscoverFoundEventArgs(BluetoothDevice device) {
        this.device = device;
    }

    @NonNull
    @Override
    public String toString() {
        return "(name: " + device.getName() + " address: " + device.getAddress() + ")";
    }
}

