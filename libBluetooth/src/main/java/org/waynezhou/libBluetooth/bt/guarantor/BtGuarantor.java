package org.waynezhou.libBluetooth.bt.guarantor;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libBluetooth.bt.enabler.BtEnabler;
import org.waynezhou.libUtil.checker.PermissionChecker;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtGuarantor {
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<Void> guaranteed = new EventHolder<>();
        @NonNull
        public final EventHolder<BtNotGuaranteedReason> notGuaranteed = new EventHolder<>();
        
        @NonNull
        Invoker getPrivateInvoker() {
            return getInvoker();
        }
    }
    
    @NonNull
    private final EventGroup eventGroup = new EventGroup();
    
    @NonNull
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    
    @NonNull
    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }
    
    private final PermissionChecker checker;
    private final BtEnabler enabler;
    
    public BtGuarantor(AppCompatActivity activity) {
        invoker = eventGroup.getPrivateInvoker();
        this.enabler = new BtEnabler(activity);
        this.checker = new PermissionChecker(activity, true,
          Manifest.permission.BLUETOOTH,
          Manifest.permission.BLUETOOTH_ADMIN,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION
        );
    }
    
    //@Override
    public void fire() {
        checker.getEvents()
          .once(g -> g.permissionDenied, $ -> {
              invoker.invoke(
                g -> g.notGuaranteed,
                BtNotGuaranteedReason.GrantedNoPermission);
          })
          .once(g -> g.permissionGranted, $ -> {
              enabler.getEvents()
                .once(g->g.agree, $$ -> {
                    invoker.invoke(
                      g -> g.guaranteed,
                      null);
                })
                .once(g->g.disagree, $$ -> {
                    invoker.invoke(
                      g -> g.notGuaranteed,
                      BtNotGuaranteedReason.BluetoothNotEnabled);
                });
              enabler.fire();
          });
        checker.fire();
    }
}
