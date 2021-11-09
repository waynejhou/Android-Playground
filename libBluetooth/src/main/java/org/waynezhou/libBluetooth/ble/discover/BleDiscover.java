package org.waynezhou.libBluetooth.ble.discover;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.event.BaseEventGroup;

import java.util.List;

public class BleDiscover {
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<BleDiscoverBatchResultEventArgs> gotBatchResult = new EventHolder<>();
        @NonNull
        public final EventHolder<BleDiscoverFailedEventArgs> failed = new EventHolder<>();
        @NonNull
        public final EventHolder<BleDiscoverResultEventArgs> gotResult = new EventHolder<>();
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

    private final boolean skipNamelessDevice;
    private final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result == null) return;
            if (skipNamelessDevice && result.getDevice().getName() == null) return;
            invoker.invoke(g->g.gotResult,
                    new BleDiscoverResultEventArgs(callbackType, result));
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (results == null || results.isEmpty()) return;
            invoker.invoke(g->g.gotBatchResult,
                    new BleDiscoverBatchResultEventArgs(results));
        }

        @Override
        public void onScanFailed(int errorCode) {
            LogHelper.e("Ble Discover Failed with code: " + errorCode + ".");
            invoker.invoke(g->g.failed,
                    new BleDiscoverFailedEventArgs(errorCode));
        }
    };

    public BleDiscover(boolean skipNamelessDevice) {
        invoker = eventGroup.getPrivateInvoker();
        this.skipNamelessDevice = skipNamelessDevice;
    }

    public BleDiscoverHandler startOn(Context context, BluetoothManager btManager) {
        BluetoothAdapter adapter = btManager.getAdapter();
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        scanner.startScan(callback);
        return new BleDiscoverHandler(scanner, callback);
    }
}
