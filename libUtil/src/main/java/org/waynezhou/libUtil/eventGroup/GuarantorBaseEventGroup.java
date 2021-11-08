package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

@Deprecated
public class GuarantorBaseEventGroup<T, TErrorReason> extends BaseEventGroup<GuarantorBaseEventGroup<T, TErrorReason>> {
    public final EventHolder<T> guaranteed = new EventHolder<>();
    public final EventHolder<TErrorReason> notGuaranteed = new EventHolder<>();
}
