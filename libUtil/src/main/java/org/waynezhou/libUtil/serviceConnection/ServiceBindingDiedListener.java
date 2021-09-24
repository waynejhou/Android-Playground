package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;

public interface ServiceBindingDiedListener {
    void invoke(ComponentName name);
}
