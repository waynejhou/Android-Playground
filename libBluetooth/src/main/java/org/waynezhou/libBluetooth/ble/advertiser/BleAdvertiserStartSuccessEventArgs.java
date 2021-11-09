package org.waynezhou.libBluetooth.ble.advertiser;

import android.bluetooth.le.AdvertiseSettings;

public class BleAdvertiserStartSuccessEventArgs {
    public final AdvertiseSettings settingsInEffect;

    public BleAdvertiserStartSuccessEventArgs(AdvertiseSettings settingsInEffect) {
        this.settingsInEffect = settingsInEffect;
    }
}
