package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.eventArgs.ActivityResultEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

@Deprecated
public class ActivityResultRegisterBaseEventGroup extends BaseEventGroup<ActivityResultRegisterBaseEventGroup> {
    public final EventHolder<ActivityResultEventArgs> result = new EventHolder<>();

}
