package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.eventArgs.RuntimeExecResultEventArgs;
import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class RuntimeExecEventGroup extends EventGroup<RuntimeExecEventGroup> {
    public final EventHolder<RuntimeExecResultEventArgs> execSuccess = new EventHolder<>();
    public final EventHolder<RuntimeExecResultEventArgs> execFailure = new EventHolder<>();
}


