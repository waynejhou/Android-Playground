package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class BleGattServerDescriptorWriteRequestEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int requestId;
    public final BluetoothGattDescriptor descriptor;
    public final boolean preparedWrite;
    public final boolean responseNeeded;
    public final int offset;
    public final byte[] value;

    public BleGattServerDescriptorWriteRequestEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        this.gattServer = gattServer;
        this.device = device;
        this.requestId = requestId;
        this.descriptor = descriptor;
        this.preparedWrite = preparedWrite;
        this.responseNeeded = responseNeeded;
        this.offset = offset;
        this.value = value;
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;

        BleGattServerDescriptorWriteRequestEventArgs other_ = (BleGattServerDescriptorWriteRequestEventArgs) other;

        if (device != other_.device) return false;
        if (requestId != other_.requestId) return false;
        if (descriptor != other_.descriptor) return false;
        if (preparedWrite != other_.preparedWrite) return false;
        if (responseNeeded != other_.responseNeeded) return false;
        if (offset != other_.offset) return false;
        return Arrays.equals(value, other_.value);
    }

    @Override
    public int hashCode() {
        int result = device.hashCode();
        result = 31 * result + requestId;
        result = 31 * result + descriptor.hashCode();
        result = 31 * result + (preparedWrite ? 1 : 0);
        result = 31 * result + (responseNeeded ? 1 : 0);
        result = 31 * result + offset;
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

}
