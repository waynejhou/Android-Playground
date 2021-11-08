package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;

@Deprecated
public interface LocalBinderServiceConnectedListener <TLocalBinder>{
    void invoke(ComponentName name, TLocalBinder binder);
}
