package org.waynezhou.libBluetooth.eventGroup;

import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverFinishedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverFoundEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BluetoothDiscoverStartedEventArgs;
import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BluetoothDiscoverEventGroup extends EventGroup<BluetoothDiscoverEventGroup> {
    public final EventHolder<BluetoothDiscoverFoundEventArgs> found = new EventHolder<>();
    public final EventHolder<BluetoothDiscoverStartedEventArgs> started = new EventHolder<>();
    public final EventHolder<BluetoothDiscoverFinishedEventArgs> finished = new EventHolder<>();
}

