package org.waynezhou.libBluetooth.ble.gatt;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleGattService {
    final UUID uuid;
    final List<BleGattCharacteristic> characteristics = new ArrayList<>();
    int serviceType = BluetoothGattService.SERVICE_TYPE_PRIMARY;

    public BleGattService(UUID uuid) {
        this.uuid = uuid;
    }

    public BleGattService add(BleGattCharacteristic characteristic) {
        characteristics.add(characteristic);
        return this;
    }

    public BleGattService setServiceType(int serviceType) {
        this.serviceType = serviceType;
        return this;
    }
}
