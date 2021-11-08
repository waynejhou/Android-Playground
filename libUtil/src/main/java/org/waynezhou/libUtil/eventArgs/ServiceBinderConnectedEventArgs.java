package org.waynezhou.libUtil.eventArgs;

import android.content.ComponentName;

@Deprecated
public class ServiceBinderConnectedEventArgs<TLocalBinder> {
    public final ComponentName name;
    public final TLocalBinder binder;

    public ServiceBinderConnectedEventArgs(ComponentName name, TLocalBinder binder) {
        this.name = name;
        this.binder = binder;
    }
}
