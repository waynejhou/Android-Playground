package org.waynezhou.libBluetooth.eventGroup;

import org.waynezhou.libBluetooth.eventArgs.BleDiscoverBatchResultEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleDiscoverFailedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleDiscoverResultEventArgs;
import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BleDiscoverEventGroup extends EventGroup<BleDiscoverEventGroup> {
    public final EventHolder<BleDiscoverBatchResultEventArgs> gotBatchResult = new EventHolder<>();
    public final EventHolder<BleDiscoverFailedEventArgs> failed = new EventHolder<>();
    public final EventHolder<BleDiscoverResultEventArgs> gotResult = new EventHolder<>();

}

