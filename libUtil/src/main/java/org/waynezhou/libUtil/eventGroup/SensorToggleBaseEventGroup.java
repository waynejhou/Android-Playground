package org.waynezhou.libUtil.eventGroup;


import org.waynezhou.libUtil.eventArgs.SensorAccuracyChangedEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorAdditionalInfoEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorFlushCompletedEventArgs;
import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

@Deprecated
public class SensorToggleBaseEventGroup extends BaseEventGroup<SensorToggleBaseEventGroup> {
    public final EventHolder<SensorChangedEventArgs> changed = new EventHolder<>();
    public final EventHolder<SensorAccuracyChangedEventArgs> accuracyChanged = new EventHolder<>();
    public final EventHolder<SensorAdditionalInfoEventArgs> additionalInfo = new EventHolder<>();
    public final EventHolder<SensorFlushCompletedEventArgs> flushCompleted = new EventHolder<>();

}

