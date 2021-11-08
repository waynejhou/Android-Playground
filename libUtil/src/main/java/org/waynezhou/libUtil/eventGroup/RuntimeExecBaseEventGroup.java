package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.eventArgs.RuntimeExecResultEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

@Deprecated
public class RuntimeExecBaseEventGroup extends BaseEventGroup<RuntimeExecBaseEventGroup> {
    public final EventHolder<RuntimeExecResultEventArgs> execSuccess = new EventHolder<>();
    public final EventHolder<RuntimeExecResultEventArgs> execFailure = new EventHolder<>();
}


