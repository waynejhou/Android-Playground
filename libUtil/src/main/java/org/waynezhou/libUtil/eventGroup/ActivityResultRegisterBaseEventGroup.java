package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.eventArgs.ActivityResultEventArgs;
import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class ActivityResultRegisterEventGroup extends EventGroup<ActivityResultRegisterEventGroup> {
    public final EventHolder<ActivityResultEventArgs> result = new EventHolder<>();

}
