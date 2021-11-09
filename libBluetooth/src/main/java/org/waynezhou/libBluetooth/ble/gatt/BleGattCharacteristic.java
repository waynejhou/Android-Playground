package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleGattCharacteristic {
    final UUID uuid;
    final List<BleGattDescriptor> descriptors = new ArrayList<>();
    int properties = BluetoothGattCharacteristic.PROPERTY_READ;
    int permissions = BluetoothGattCharacteristic.PERMISSION_READ;
    int writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;

    public BleGattCharacteristic(UUID uuid) {
        this.uuid = uuid;
    }

    public BleGattCharacteristic add(BleGattDescriptor descriptor) {
        descriptors.add(descriptor);
        return this;
    }

    public BleGattCharacteristic addProperty(int property) {
        this.properties = properties | property;
        return this;
    }

    public BleGattCharacteristic setProperties(int properties) {
        this.properties = properties;
        return this;
    }

    public BleGattCharacteristic addPermission(int permission) {
        this.permissions = permissions | permission;
        return this;
    }

    public BleGattCharacteristic setPermission(int permissions) {
        this.permissions = permissions;
        return this;
    }

    public BleGattCharacteristic setWriteType(int writeType) {
        this.writeType = writeType;
        return this;
    }
}
