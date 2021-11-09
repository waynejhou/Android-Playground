package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;

public class BleGattServerDescriptorReadRequestEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int requestId;
    public final int offset;
    public final BluetoothGattDescriptor descriptor;

    public BleGattServerDescriptorReadRequestEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        this.gattServer = gattServer;
        this.device = device;
        this.requestId = requestId;
        this.offset = offset;
        this.descriptor = descriptor;
    }
}
