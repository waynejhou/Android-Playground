package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGatt;

public class BleGattClientHandle {
    final BluetoothGatt gatt;

    public BleGattClientHandle(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public void close(){
        gatt.disconnect();
        gatt.close();
    }
}
