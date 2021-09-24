package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;

public interface LocalBinderServiceConnectedListener <TLocalBinder>{
    void invoke(ComponentName name, TLocalBinder binder);
}
