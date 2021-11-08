package org.waynezhou.libBluetooth;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtEnablerBaseEventGroup extends BaseEventGroup<BtEnablerBaseEventGroup> {
    private final EventHolder<Object> agree = new EventHolder<>();
    private final EventHolder<Object> disagree = new EventHolder<>();

    public EventHolder<Object> getAgree() {
        return agree;
    }

    public EventHolder<Object> getDisagree() {
        return disagree;
    }
}
