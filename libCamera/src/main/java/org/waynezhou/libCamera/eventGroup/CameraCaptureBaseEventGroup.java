package org.waynezhou.libCamera.eventGroup;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class CameraCaptureBaseEventGroup extends BaseEventGroup<CameraCaptureBaseEventGroup> {
    public final EventHolder<CapturedEventArgs> captured = new EventHolder<>();
    public static class CapturedEventArgs{
        public final byte[] bytes;

        public CapturedEventArgs(byte[] bytes) {
            this.bytes = bytes;
        }
    }
}
