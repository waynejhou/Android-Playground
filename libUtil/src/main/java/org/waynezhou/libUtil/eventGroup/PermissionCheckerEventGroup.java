package org.waynezhou.libUtil.eventGroup;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

import java.util.List;

public class PermissionCheckerEventGroup extends EventGroup<PermissionCheckerEventGroup> {
    public final EventHolder<List<String>> permissionGranted = new EventHolder<>();
    public final EventHolder<List<String>> permissionDenied = new EventHolder<>();
}
