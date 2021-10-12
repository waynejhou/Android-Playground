package org.waynezhou.androidplayground.service;

import org.waynezhou.libBluetooth.service.SingleBleDeviceForegroundService;

public class BleControllerService extends SingleBleDeviceForegroundService {
    
    @Override
    protected int getNotificationSmallIcon() {
        return 0;
    }
    
    @Override
    protected String getGattServiceUUID() {
        return null;
    }
    
    @Override
    protected String getReceiverCharacteristicUUID() {
        return null;
    }
}
