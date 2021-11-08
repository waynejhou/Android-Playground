package org.waynezhou.libBluetooth.eventGroup;

import org.waynezhou.libBluetooth.eventArgs.BleAdvertiserStartFailureEventArgs;
import org.waynezhou.libBluetooth.eventArgs.BleAdvertiserStartSuccessEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BleAdvertiserBaseEventGroup extends BaseEventGroup<BleAdvertiserBaseEventGroup> {
    public final EventHolder<BleAdvertiserStartFailureEventArgs> startFailure = new EventHolder<>();
    public final EventHolder<BleAdvertiserStartSuccessEventArgs> startSuccess = new EventHolder<>();

}


