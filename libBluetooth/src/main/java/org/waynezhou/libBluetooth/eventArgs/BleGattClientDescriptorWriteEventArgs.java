package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

public class BleGattClientDescriptorWriteEventArgs {
    public final BluetoothGatt gatt;
    public final BluetoothGattDescriptor descriptor;
    public final int status;

    public BleGattClientDescriptorWriteEventArgs(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        this.gatt = gatt;
        this.descriptor = descriptor;
        this.status = status;
    }
}
