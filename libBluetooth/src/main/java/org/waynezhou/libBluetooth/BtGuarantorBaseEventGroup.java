package org.waynezhou.libBluetooth;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtGuarantorEventGroup extends EventGroup<BtGuarantorEventGroup> {
    public final EventHolder<Object> guaranteed = new EventHolder<>();
    public final EventHolder<BtNotGuaranteedReason> notGuaranteed = new EventHolder<>();
}
