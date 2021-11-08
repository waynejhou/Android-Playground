package org.waynezhou.libUtil.eventGroup;

import android.content.ComponentName;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.eventArgs.ServiceBinderConnectedEventArgs;

@Deprecated
public class ServiceBinderBaseEventGroup<TServiceLocalBinder> extends BaseEventGroup<ServiceBinderBaseEventGroup<TServiceLocalBinder>> {
    public final EventHolder<ServiceBinderConnectedEventArgs<TServiceLocalBinder>> connected = new EventHolder<>();
    public final EventHolder<ComponentName> disconnected = new EventHolder<>();
    public final EventHolder<ComponentName> bindingDied = new EventHolder<>();
    public final EventHolder<ComponentName> nullBinding = new EventHolder<>();
    public final EventHolder<Object> connectionFailed = new EventHolder<>();

}
