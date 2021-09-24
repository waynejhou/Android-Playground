package org.waynezhou.libBluetooth.eventArgs;

import android.bluetooth.le.AdvertiseSettings;

public class BleAdvertiserStartSuccessEventArgs {
    public final AdvertiseSettings settingsInEffect;

    public BleAdvertiserStartSuccessEventArgs(AdvertiseSettings settingsInEffect) {
        this.settingsInEffect = settingsInEffect;
    }
}
