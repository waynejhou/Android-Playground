package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerPhyUpdateEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int txPhy;
    public final int rxPhy;
    public final int status;

    public BleGattServerPhyUpdateEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int txPhy, int rxPhy, int status) {
        this.gattServer = gattServer;
        this.device = device;
        this.txPhy = txPhy;
        this.rxPhy = rxPhy;
        this.status = status;
    }
}
