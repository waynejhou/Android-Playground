package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerCharacteristicReadRequestEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int requestId;
    public final int offset;
    public final BluetoothGattCharacteristic characteristic;

    public BleGattServerCharacteristicReadRequestEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        this.gattServer = gattServer;
        this.device = device;
        this.requestId = requestId;
        this.offset = offset;
        this.characteristic = characteristic;
    }
}
