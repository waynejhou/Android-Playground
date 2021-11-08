package org.waynezhou.libUtil.eventArgs;

import android.hardware.Sensor;

@Deprecated
public class SensorAccuracyChangedEventArgs {
    public final Sensor sensor;
    public final int accuracy;

    public SensorAccuracyChangedEventArgs(Sensor sensor, int accuracy) {
        this.sensor = sensor;
        this.accuracy = accuracy;
    }
}
