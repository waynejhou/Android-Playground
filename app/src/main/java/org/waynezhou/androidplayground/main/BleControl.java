package org.waynezhou.androidplayground.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.waynezhou.androidplayground.service.BleControllerService;
import org.waynezhou.libBluetooth.bt.guarantor.BtGuarantor;
import org.waynezhou.libUtil.standard.StandardKt;
import org.waynezhou.libUtil.register.BroadcastReceiverRegister;
import org.waynezhou.libUtil.log.LogHelper;

public class BleControl {
    private MainActivity host;
    
    void init(MainActivity activity) {
        this.host = activity;
        this.host.getEvents().on(g->g.create, this::onHostCreate);
        this.host.getEvents().on(g->g.destroy, this::onHostDestroy);
    }
    
    private void onHostDestroy(Void unused) {
        host.stopService(new Intent(host, BleControllerService.class));
    }
    
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private BroadcastReceiverRegister broadcastReceiverRegister;
    
    private void onHostCreate(Bundle bundle) {
        BtGuarantor btGuarantor = new BtGuarantor(host);
        btGuarantor.getEvents()
          .on(g -> g.guaranteed, e -> {
              ComponentName name = host.startService(new Intent(host, BleControllerService.class));
              LogHelper.i(name);
          })
          .on(g -> g.notGuaranteed, e -> {
              LogHelper.e("Bluetooth error");
          });
        btGuarantor.fire();
    
        broadcastReceiverRegister = StandardKt.apply(
          new BroadcastReceiverRegister(host, new IntentFilter(BleControllerService.BROADCAST_GOT_CMD)), it->{
              it.getEvents().on(g->g.result, e->{
                  LogHelper.d(e.intent.getStringExtra(BleControllerService.BROADCAST_GOT_CMD_EXTRA_DATA_NAME));
              });
          });
    }
}
