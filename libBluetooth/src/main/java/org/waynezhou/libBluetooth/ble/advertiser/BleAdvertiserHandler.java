package org.waynezhou.libBluetooth.ble.advertiser;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;

public class BleAdvertiserHandler {
    final BluetoothLeAdvertiser advertiser;
    final AdvertiseCallback callback;

    public BleAdvertiserHandler(BluetoothLeAdvertiser advertiser, AdvertiseCallback callback) {
        this.advertiser = advertiser;
        this.callback = callback;
    }

    public void stop() {
        advertiser.stopAdvertising(callback);
    }
}
