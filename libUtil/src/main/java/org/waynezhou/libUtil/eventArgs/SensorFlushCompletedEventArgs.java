package org.waynezhou.libUtil.eventArgs;

import android.hardware.Sensor;

public class SensorFlushCompletedEventArgs {
    public final Sensor sensor;

    public SensorFlushCompletedEventArgs(Sensor sensor) {
        this.sensor = sensor;
    }
}
