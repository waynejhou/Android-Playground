package org.waynezhou.libBluetooth;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtEnablerEventGroup extends EventGroup<BtEnablerEventGroup> {
    private final EventHolder<Object> agree = new EventHolder<>();
    private final EventHolder<Object> disagree = new EventHolder<>();

    public EventHolder<Object> getAgree() {
        return agree;
    }

    public EventHolder<Object> getDisagree() {
        return disagree;
    }
}
