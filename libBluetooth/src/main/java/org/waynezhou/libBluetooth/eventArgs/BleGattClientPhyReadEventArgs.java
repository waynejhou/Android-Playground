package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;

public class BleGattClientPhyReadEventArgs {
    public final BluetoothGatt gatt;
    public final int txPhy;
    public final int rxPhy;
    public final int status;

    public BleGattClientPhyReadEventArgs(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        this.gatt = gatt;
        this.txPhy = txPhy;
        this.rxPhy = rxPhy;
        this.status = status;
    }
}
