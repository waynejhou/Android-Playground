package org.waynezhou.libBluetooth.ble.advertiser;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BleAdvertiser {
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<BleAdvertiserStartFailureEventArgs> startFailure = new EventHolder<>();
        @NonNull
        public final EventHolder<BleAdvertiserStartSuccessEventArgs> startSuccess = new EventHolder<>();
        @NonNull
        Invoker getPrivateInvoker(){return getInvoker();}
    }
    
    @NonNull
    private final EventGroup eventGroup = new EventGroup();
    
    @NonNull
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    

    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }

    private final AdvertiseSettings settings;
    private final AdvertiseData data;
    private final AdvertiseCallbackClass callback = new AdvertiseCallbackClass();

    public BleAdvertiser(AdvertiseSettings settings, AdvertiseData data) {
        invoker = eventGroup.getPrivateInvoker();
        this.settings = settings;
        this.data = data;
    }

    public BleAdvertiserHandler startOn(BluetoothManager btManager) {
        BluetoothLeAdvertiser advertiser = btManager.getAdapter().getBluetoothLeAdvertiser();
        advertiser.startAdvertising(settings, data, callback);
        return new BleAdvertiserHandler(advertiser, callback);
    }

    private class AdvertiseCallbackClass extends AdvertiseCallback {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            invoker.invoke(g->g.startSuccess,
                    new BleAdvertiserStartSuccessEventArgs(
                            settingsInEffect
                    )
            );
        }

        @Override
        public void onStartFailure(int errorCode) {
            invoker.invoke(g->g.startFailure,
                    new BleAdvertiserStartFailureEventArgs(errorCode)
            );
        }
    }
}

