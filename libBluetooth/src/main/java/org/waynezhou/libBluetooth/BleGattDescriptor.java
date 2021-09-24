package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

public class BleGattDescriptor {
    final UUID uuid;
    int permissions = BluetoothGattDescriptor.PERMISSION_READ;

    public BleGattDescriptor(UUID uuid) {
        this.uuid = uuid;
    }

    public BleGattDescriptor addPermissions(int permission) {
        this.permissions = permissions | permission;
        return this;
    }

    public BleGattDescriptor setPermissions(int permissions) {
        this.permissions = permissions;
        return this;
    }
}
