package org.waynezhou.libBluetooth;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.PermissionChecker;
import org.waynezhou.libUtil.event.EventGroup;

public class BtGuarantor /*extends EventAction<BluetoothGuarantor, BluetoothGuarantorEventGroup>*/ {
    
    private final _BtGuarantorEventGroup eventGroup = new _BtGuarantorEventGroup();
    
    private static class _BtGuarantorEventGroup extends BtGuarantorEventGroup {
        public EventGroup<BtGuarantorEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }
    
    public BtGuarantorEventGroup getEventGroup() {
        return eventGroup;
    }
    
    private final PermissionChecker checker;
    private final BtEnabler enabler;
    
    public BtGuarantor(AppCompatActivity activity) {
        //super(new BluetoothGuarantorEventGroup());
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
        checker.getEventGroup()
          .once(g -> g.permissionDenied, $ -> {
              eventGroup.getInvoker().invoke(
                g -> g.notGuaranteed,
                BtNotGuaranteedReason.GrantedNoPermission);
          })
          .once(g -> g.permissionGranted, $ -> {
              enabler.getEventGroup()
                .once(BtEnablerEventGroup::getAgree, $$ -> {
                    eventGroup.getInvoker().invoke(
                      g -> g.guaranteed,
                      null);
                })
                .once(BtEnablerEventGroup::getDisagree, $$ -> {
                    eventGroup.getInvoker().invoke(
                      g -> g.notGuaranteed,
                      BtNotGuaranteedReason.BluetoothNotEnabled);
                });
              enabler.fire();
          });
        checker.fire();

        /*
        checker.set(x->x.getPermissionDenied(), $ -> {
            this.eventPack.getNotGuaranteed().invoke(BluetoothNotGuaranteedReason.GrantedNoPermission);
            this.eventPack.clear();
        }).set(x->x.getPermissionGranted(), $ -> {
            enabler.set(x->x.getAgree(), $$->{
                this.eventPack.getGuaranteed().invoke(null);
                this.eventPack.clear();
            }).set(x->x.getDisagree(), $$->{
                this.eventPack.getNotGuaranteed().invoke(BluetoothNotGuaranteedReason.BluetoothNotEnabled);
                this.eventPack.clear();
            }).fire();
        }).fire();
         */
    }
}


/*public class BluetoothGuarantor {
    private final PermissionChecker checker;
    private final BluetoothEnabler enabler;

    private final BluetoothGuaranteedListener defaultGuaranteedListener = () -> {
    };

    private final BluetoothNotGuaranteedListener defaultNotGuaranteedListener = ($) -> {
    };

    private BluetoothGuaranteedListener guaranteedListener = defaultGuaranteedListener;

    private BluetoothNotGuaranteedListener notGuaranteedListener = defaultNotGuaranteedListener;

    public BluetoothGuarantor(AppCompatActivity activity) {
        this.enabler = new BluetoothEnabler(activity);
        this.checker = new PermissionChecker(activity,
                new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, true);
    }

    public BluetoothGuarantor onceGuaranteed(BluetoothGuaranteedListener listener) {
        guaranteedListener = listener;
        return this;
    }

    public BluetoothGuarantor onceNotGuaranteed(BluetoothNotGuaranteedListener listener) {
        notGuaranteedListener = listener;
        return this;
    }

    private void clearListener() {
        notGuaranteedListener = defaultNotGuaranteedListener;
        guaranteedListener = defaultGuaranteedListener;
    }

    public void fire(AppCompatActivity activity) {
        checker.once(x->x.getPermissionDenied(),$ -> {
            notGuaranteedListener.invoke(BluetoothNotGuaranteedReason.GrantedNoPermission);
            clearListener();
        }).once(x->x.getPermissionGranted(), $ -> {
            enabler.onceAgree(()->{
                guaranteedListener.invoke();
                clearListener();
            }).onceDisagree(()->{
                notGuaranteedListener.invoke(BluetoothNotGuaranteedReason.BluetoothNotEnabled);
                clearListener();
            }).fire(activity);
        }).fire();
    }
}

*/