package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverFinishedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverFoundEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverStartedEventArgs;
import org.waynezhou.libBluetooth.eventGroup.BluetoothDiscoverBaseEventGroup;
import org.waynezhou.libUtil.event.BaseEventGroup;

public class BtDiscover {
    private final _BluetoothDiscoverBaseEventGroup eventGroup = new _BluetoothDiscoverBaseEventGroup();
    private final BaseEventGroup<BluetoothDiscoverBaseEventGroup>.Invoker invoker;

    private static class _BluetoothDiscoverBaseEventGroup extends BluetoothDiscoverBaseEventGroup {
        @NonNull
        public BaseEventGroup<BluetoothDiscoverBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BluetoothDiscoverBaseEventGroup getEventGroup() {
        return eventGroup;
    }
    private final boolean skipNamelessDevice;
    public BtDiscover(boolean skipNamelessDevice){
        this.invoker = eventGroup.getInvoker();
        this.skipNamelessDevice = skipNamelessDevice;
    }
    public BtDiscoverHandler startOn(Context context, BluetoothManager btManager){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(
                new BroadcastReceiver(){
                    @Override
                    public void onReceive(
                      Context context,
                      Intent intent
                    ) {
                        if(intent!=null){
                            switch (intent.getAction()){
                                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                                    invoker.invoke(g->g.started, new BluetoothDiscoverStartedEventArgs());
                                    break;
                                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                                    invoker.invoke(g->g.finished, new BluetoothDiscoverFinishedEventArgs());
                                    break;
                                case BluetoothDevice.ACTION_FOUND:
                                    BluetoothDevice device =
                                      intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                    if(skipNamelessDevice && device.getName() == null) return;
                                    invoker.invoke(g->g.found,
                                      new BluetoothDiscoverFoundEventArgs(device)
                                    );
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                , filter
        );
        BluetoothAdapter adapter = btManager.getAdapter();
        adapter.startDiscovery();
        return new BtDiscoverHandler(adapter);
    }
}
