package org.waynezhou.libBluetooth.ble.discover;

public class BleDiscoverFailedEventArgs {
    public final int errorCode;

    public BleDiscoverFailedEventArgs(int errorCode) {
        this.errorCode = errorCode;
    }
}
