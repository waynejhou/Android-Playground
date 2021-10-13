package org.waynezhou.androidplayground.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import org.waynezhou.androidplayground.service.BleControllerService;
import org.waynezhou.libBluetooth.BtGuarantor;
import org.waynezhou.libUtil.BroadcastReceiverRegister;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SensorToggle;

public class BleControl {
    private Activity host;
    
    void init(Activity activity) {
        this.host = activity;
        this.host.getEventGroup().on(g->g.create, this::onHostCreate);
        this.host.getEventGroup().on(g->g.destroy, this::onHostDestroy);
    }
    
    private void onHostDestroy(Void unused) {
        host.stopService(new Intent(host, BleControllerService.class));
    }
    
    
    private void onHostCreate(Bundle bundle) {
        BtGuarantor btGuarantor = new BtGuarantor(host);
        btGuarantor.getEventGroup()
          .on(g -> g.guaranteed, e -> {
              ComponentName name = host.startService(new Intent(host, BleControllerService.class));
              LogHelper.i(name);
          })
          .on(g -> g.notGuaranteed, e -> {
              LogHelper.e("Bluetooth error");
          });
        btGuarantor.fire();
    
        BroadcastReceiverRegister broadcastReceiverRegister = new BroadcastReceiverRegister();
        IntentFilter filter = new IntentFilter(BleControllerService.BROADCAST_GOT_CMD);
        broadcastReceiverRegister.setBroadReceive((ctx, intent) -> {
            LogHelper.d(intent.getStringExtra(BleControllerService.BROADCAST_GOT_CMD_EXTRA_DATA_NAME));
        });
        host.registerReceiver(broadcastReceiverRegister, filter);
    }
}
