package org.waynezhou.libBluetooth;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BtGuarantorBaseEventGroup extends BaseEventGroup<BtGuarantorBaseEventGroup> {
    public final EventHolder<Object> guaranteed = new EventHolder<>();
    public final EventHolder<BtNotGuaranteedReason> notGuaranteed = new EventHolder<>();
}
