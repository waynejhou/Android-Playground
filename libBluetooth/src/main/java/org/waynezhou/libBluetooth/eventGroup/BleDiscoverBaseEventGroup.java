package org.waynezhou.libBluetooth.eventGroup;

import org.waynezhou.libBluetooth.eventArgs.BleDiscoverBatchResultEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleDiscoverFailedEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleDiscoverResultEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BleDiscoverBaseEventGroup extends BaseEventGroup<BleDiscoverBaseEventGroup> {
    public final EventHolder<BleDiscoverBatchResultEventArgs> gotBatchResult = new EventHolder<>();
    public final EventHolder<BleDiscoverFailedEventArgs> failed = new EventHolder<>();
    public final EventHolder<BleDiscoverResultEventArgs> gotResult = new EventHolder<>();

}

