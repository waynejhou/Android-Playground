package org.waynezhou.libBluetooth.bt.discover;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtDiscover {
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<BluetoothDiscoverFoundEventArgs> found = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothDiscoverStartedEventArgs> started = new EventHolder<>();
        @NonNull
        public final EventHolder<BluetoothDiscoverFinishedEventArgs> finished = new EventHolder<>();
        
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
    
    private final boolean skipNamelessDevice;
    
    public BtDiscover(boolean skipNamelessDevice) {
        this.invoker = eventGroup.getPrivateInvoker();
        this.skipNamelessDevice = skipNamelessDevice;
    }
    
    public BtDiscoverHandler startOn(
      Context context,
      BluetoothManager btManager
    ) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(
          new BroadcastReceiver() {
              @Override
              public void onReceive(
                Context context,
                Intent intent
              ) {
                  if (intent != null) {
                      switch (intent.getAction()) {
                          case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                              invoker.invoke(g -> g.started, new BluetoothDiscoverStartedEventArgs());
                              break;
                          case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                              invoker.invoke(g -> g.finished, new BluetoothDiscoverFinishedEventArgs());
                              break;
                          case BluetoothDevice.ACTION_FOUND:
                              BluetoothDevice device =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                              if (skipNamelessDevice && device.getName() == null) return;
                              invoker.invoke(g -> g.found,
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
