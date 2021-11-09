package org.waynezhou.libBluetooth.ble.gatt;

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
