package org.waynezhou.libCamera.eventGroup;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class CameraCaptureEventGroup extends EventGroup<CameraCaptureEventGroup> {
    public final EventHolder<CapturedEventArgs> captured = new EventHolder<>();
    public static class CapturedEventArgs{
        public final byte[] bytes;

        public CapturedEventArgs(byte[] bytes) {
            this.bytes = bytes;
        }
    }
}
