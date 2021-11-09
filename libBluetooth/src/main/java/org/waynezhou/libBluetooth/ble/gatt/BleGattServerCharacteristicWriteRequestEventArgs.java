package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class BleGattServerCharacteristicWriteRequestEventArgs {
    public final BluetoothGattServer gattServer;
    public final BluetoothDevice device;
    public final int requestId;
    public final BluetoothGattCharacteristic characteristic;
    public final boolean preparedWrite;
    public final boolean responseNeeded;
    public final int offset;
    public final byte[] value;

    public BleGattServerCharacteristicWriteRequestEventArgs(BluetoothGattServer gattServer, BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        this.gattServer = gattServer;
        this.device = device;
        this.requestId = requestId;
        this.characteristic = characteristic;
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
        BleGattServerCharacteristicWriteRequestEventArgs other_ = (BleGattServerCharacteristicWriteRequestEventArgs) other;
        if (device != other_.device) return false;
        if (requestId != other_.requestId) return false;
        if (characteristic != other_.characteristic) return false;
        if (preparedWrite != other_.preparedWrite) return false;
        if (responseNeeded != other_.responseNeeded) return false;
        if (offset != other_.offset) return false;
        return Arrays.equals(value, other_.value);
    }

    @Override
    public int hashCode() {
        int result = device.hashCode();
        result = 31 * result + requestId;
        result = 31 * result + characteristic.hashCode();
        result = 31 * result + (preparedWrite ? 1 : 0);
        result = 31 * result + (responseNeeded ? 1 : 0);
        result = 31 * result + offset;
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

}
