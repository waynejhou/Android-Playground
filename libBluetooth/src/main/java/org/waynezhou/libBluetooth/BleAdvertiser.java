package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;

import androidx.annotation.NonNull;

import org.waynezhou.libBluetooth.eventGroup.BleAdvertiserBaseEventGroup;
import org.waynezhou.libBluetooth.eventArgs.BleAdvertiserStartFailureEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleAdvertiserStartSuccessEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;

public class BleAdvertiser {

    private final _BleAdvertiserBaseEventGroup eventGroup = new _BleAdvertiserBaseEventGroup();

    private static class _BleAdvertiserBaseEventGroup extends BleAdvertiserBaseEventGroup {
        @NonNull
        public BaseEventGroup<BleAdvertiserBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BleAdvertiserBaseEventGroup getEventGroup() {
        return eventGroup;
    }

    private final AdvertiseSettings settings;
    private final AdvertiseData data;
    private final AdvertiseCallbackClass callback = new AdvertiseCallbackClass();

    public BleAdvertiser(AdvertiseSettings settings, AdvertiseData data) {
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
            eventGroup.getInvoker().invoke(g->g.startSuccess,
                    new BleAdvertiserStartSuccessEventArgs(
                            settingsInEffect
                    )
            );
        }

        @Override
        public void onStartFailure(int errorCode) {
            eventGroup.getInvoker().invoke(g->g.startFailure,
                    new BleAdvertiserStartFailureEventArgs(errorCode)
            );
        }
    }
}

