package org.waynezhou.libBluetooth.eventArgs;

public class BleDiscoverFailedEventArgs {
    public final int errorCode;

    public BleDiscoverFailedEventArgs(int errorCode) {
        this.errorCode = errorCode;
    }
}
