package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class GuarantorEventGroup<T, TErrorReason> extends EventGroup<GuarantorEventGroup<T, TErrorReason>> {
    public final EventHolder<T> guaranteed = new EventHolder<>();
    public final EventHolder<TErrorReason> notGuaranteed = new EventHolder<>();
}
