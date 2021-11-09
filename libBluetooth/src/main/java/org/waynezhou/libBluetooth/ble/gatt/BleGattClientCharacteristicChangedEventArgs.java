package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class BleGattClientCharacteristicChangedEventArgs {
    public final BluetoothGatt gatt;
    public final BluetoothGattCharacteristic characteristic;

    public BleGattClientCharacteristicChangedEventArgs(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.gatt = gatt;
        this.characteristic = characteristic;
    }
}
