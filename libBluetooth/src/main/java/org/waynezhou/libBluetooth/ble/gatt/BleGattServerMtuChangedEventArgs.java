package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerMtuChangedEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int mtu;

    public BleGattServerMtuChangedEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int mtu) {
        this.gattServer = gattServer;
        this.device = device;
        this.mtu = mtu;
    }
}
