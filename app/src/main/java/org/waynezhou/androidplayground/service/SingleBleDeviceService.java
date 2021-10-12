package org.waynezhou.androidplayground.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SingleBleDeviceService extends Service {
    public SingleBleDeviceService() {
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}