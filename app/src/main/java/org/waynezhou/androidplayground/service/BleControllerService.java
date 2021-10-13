package org.waynezhou.androidplayground.service;

import org.waynezhou.androidplayground.R;
import org.waynezhou.libBluetooth.service.SingleBleDeviceForegroundService;

public class BleControllerService extends SingleBleDeviceForegroundService {
    
    @Override
    protected int getNotificationSmallIcon() {
        return R.mipmap.ic_launcher;
    }
    
    @Override
    protected String getDeviceName() {
        return "Rotation_Test";
    }
    
    public static final String BROADCAST_GOT_CMD = "GOT_CMD";
    @Override
    protected String getBroadcastActionNameForReceive() {
        return BROADCAST_GOT_CMD;
    }
    
    public static final String BROADCAST_GOT_CMD_EXTRA_DATA_NAME = "cmd";
    @Override
    protected String getBroadcastExtraDataNameForReceiveData() {
        return BROADCAST_GOT_CMD_EXTRA_DATA_NAME;
    }
    
    @Override
    protected String getGattServiceUUID() {
        return "0000ffe0-0000-1000-8000-00805f9b34fb";
    }
    
    @Override
    protected String getReceiverCharacteristicUUID() {
        return "0000ffe1-0000-1000-8000-00805f9b34fb";
    }
}
