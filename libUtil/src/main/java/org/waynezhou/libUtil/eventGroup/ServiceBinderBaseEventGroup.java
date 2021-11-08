package org.waynezhou.libUtil.eventGroup;

import android.content.ComponentName;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.eventArgs.ServiceBinderConnectedEventArgs;

public class ServiceBinderEventGroup<TServiceLocalBinder> extends EventGroup<ServiceBinderEventGroup<TServiceLocalBinder>> {
    public final EventHolder<ServiceBinderConnectedEventArgs<TServiceLocalBinder>> connected = new EventHolder<>();
    public final EventHolder<ComponentName> disconnected = new EventHolder<>();
    public final EventHolder<ComponentName> bindingDied = new EventHolder<>();
    public final EventHolder<ComponentName> nullBinding = new EventHolder<>();
    public final EventHolder<Object> connectionFailed = new EventHolder<>();

}
