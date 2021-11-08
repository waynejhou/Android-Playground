package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;

@Deprecated
public interface ServiceBindingDiedListener {
    void invoke(ComponentName name);
}
