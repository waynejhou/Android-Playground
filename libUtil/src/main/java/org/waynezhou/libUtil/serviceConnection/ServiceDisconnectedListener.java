package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;

public interface ServiceDisconnectedListener {
    void invoke(ComponentName name);
}
