package org.waynezhou.libUtil.eventArgs;

import android.hardware.SensorAdditionalInfo;

@Deprecated
public class SensorAdditionalInfoEventArgs {
    public final SensorAdditionalInfo info;

    public SensorAdditionalInfoEventArgs(SensorAdditionalInfo info) {
        this.info = info;
    }
}
