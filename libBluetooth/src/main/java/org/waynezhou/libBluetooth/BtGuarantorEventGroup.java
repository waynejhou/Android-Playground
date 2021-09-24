package org.waynezhou.libBluetooth;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtGuarantorEventGroup extends EventGroup<BtGuarantorEventGroup> {
    private final EventHolder<Object> guaranteed = new EventHolder<>();
    private final EventHolder<BtNotGuaranteedReason> notGuaranteed = new EventHolder<>();

    public EventHolder<BtNotGuaranteedReason> getNotGuaranteed() {
        return notGuaranteed;
    }

    public EventHolder<Object> getGuaranteed() {
        return guaranteed;
    }
}
