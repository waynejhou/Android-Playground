package org.waynezhou.libUtil.eventArgs;

import android.hardware.SensorEvent;

@Deprecated
public class SensorChangedEventArgs {
    public final SensorEvent event;

    public SensorChangedEventArgs(SensorEvent event) {
        this.event = event;
    }
}
