package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class BleGattClientCharacteristicWriteEventArgs {
    public final BluetoothGatt gatt;
    public final BluetoothGattCharacteristic characteristic;
    public final int status;

    public BleGattClientCharacteristicWriteEventArgs(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        this.gatt = gatt;
        this.characteristic = characteristic;
        this.status = status;
    }
}
