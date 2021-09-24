package org.waynezhou.libUtil.eventArgs;

import android.hardware.SensorAdditionalInfo;

public class SensorAdditionalInfoEventArgs {
    public final SensorAdditionalInfo info;

    public SensorAdditionalInfoEventArgs(SensorAdditionalInfo info) {
        this.info = info;
    }
}
