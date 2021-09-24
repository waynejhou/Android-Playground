package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;

public class BleGattClientPhyUpdateEventArgs {
    public final BluetoothGatt gatt;
    public final int txPhy;
    public final int rxPhy;
    public final int status;

    public BleGattClientPhyUpdateEventArgs(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        this.gatt = gatt;
        this.txPhy = txPhy;
        this.rxPhy = rxPhy;
        this.status = status;
    }
}
