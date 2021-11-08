package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import androidx.annotation.NonNull;

import org.waynezhou.libBluetooth.eventArgs.BleDiscoverBatchResultEventArgs;
import org.waynezhou.libBluetooth.eventGroup.BleDiscoverBaseEventGroup;
import org.waynezhou.libBluetooth.eventArgs.BleDiscoverFailedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleDiscoverResultEventArgs;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.event.BaseEventGroup;

import java.util.List;

public class BleDiscover {
    private final _BleDiscoverBaseEventGroup eventGroup = new _BleDiscoverBaseEventGroup();
    private final BaseEventGroup<BleDiscoverBaseEventGroup>.Invoker invoker;
    private static class _BleDiscoverBaseEventGroup extends BleDiscoverBaseEventGroup {
        @NonNull
        public BaseEventGroup<BleDiscoverBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BleDiscoverBaseEventGroup getEventGroup() {
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
        invoker = eventGroup.getInvoker();
        this.skipNamelessDevice = skipNamelessDevice;
    }

    public BleDiscoverHandler startOn(Context context, BluetoothManager btManager) {
        BluetoothAdapter adapter = btManager.getAdapter();
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        scanner.startScan(callback);
        return new BleDiscoverHandler(scanner, callback);
    }
}
