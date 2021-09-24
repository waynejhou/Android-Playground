package org.waynezhou.libBluetooth.eventArgs;

public class BleAdvertiserStartFailureEventArgs {
    public final int errorCode;

    public BleAdvertiserStartFailureEventArgs(int errorCode) {
        this.errorCode = errorCode;
    }
}
