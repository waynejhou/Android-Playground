package org.waynezhou.libBluetooth.ble.discover;

import android.bluetooth.le.ScanResult;

public class BleDiscoverResultEventArgs {
    public final int callbackType;
    public final ScanResult result;

    public BleDiscoverResultEventArgs(int callbackType, ScanResult result) {
        this.callbackType = callbackType;
        this.result = result;
    }
}
