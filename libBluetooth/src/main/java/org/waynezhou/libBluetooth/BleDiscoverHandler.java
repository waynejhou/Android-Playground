package org.waynezhou.libBluetooth;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;

import org.waynezhou.libUtil.LogHelper;

public class BleDiscoverHandler {
    final BluetoothLeScanner scanner;
    final ScanCallback callback;

    public BleDiscoverHandler(BluetoothLeScanner scanner, ScanCallback callback) {
        this.scanner = scanner;
        this.callback = callback;
    }

    public void stop(){
        scanner.stopScan(callback);
        LogHelper.d("scanner.stopScan(callback)");
    }
}
