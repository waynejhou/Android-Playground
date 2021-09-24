package org.waynezhou.libBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;

import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverFinishedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverFoundEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverStartedEventArgs;
import org.waynezhou.libBluetooth.eventGroup.BluetoothDiscoverEventGroup;
import org.waynezhou.libUtil.BroadcastReceiverRegister;
import org.waynezhou.libUtil.event.EventGroup;

public class BtDiscover {
    private final _BluetoothDiscoverEventGroup eventGroup = new _BluetoothDiscoverEventGroup();
    private final EventGroup<BluetoothDiscoverEventGroup>.Invoker invoker;

    private static class _BluetoothDiscoverEventGroup extends BluetoothDiscoverEventGroup {
        public EventGroup<BluetoothDiscoverEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BluetoothDiscoverEventGroup getEventGroup() {
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
                new BroadcastReceiverRegister()
                .setBroadReceive(($, intent)->{
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
                })
                , filter
        );
        BluetoothAdapter adapter = btManager.getAdapter();
        adapter.startDiscovery();
        return new BtDiscoverHandler(adapter);
    }
}
