package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;

@Deprecated
public interface ServiceDisconnectedListener {
    void invoke(ComponentName name);
}
