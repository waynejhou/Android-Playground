package org.waynezhou.libBluetooth.ble.advertiser;

public class BleAdvertiserStartFailureEventArgs {
    public final int errorCode;

    public BleAdvertiserStartFailureEventArgs(int errorCode) {
        this.errorCode = errorCode;
    }
}
