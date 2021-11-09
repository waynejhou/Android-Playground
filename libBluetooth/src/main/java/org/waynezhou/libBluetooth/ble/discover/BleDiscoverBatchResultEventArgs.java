package org.waynezhou.libBluetooth.ble.discover;

import android.bluetooth.le.ScanResult;

import java.util.List;

public class BleDiscoverBatchResultEventArgs {
    public final List<ScanResult> results;

    public BleDiscoverBatchResultEventArgs(List<ScanResult> results) {
        this.results = results;
    }
}
