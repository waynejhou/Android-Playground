package org.waynezhou.libCamera;

import org.waynezhou.libCamera.eventGroup.CameraCaptureEventGroup;
import org.waynezhou.libUtil.event.EventGroup;

public abstract class CameraCapture extends CameraCaptureEventGroup {
    private final EventGroup<CameraCaptureEventGroup>.Invoker invoker;
    protected CameraCapture(){
        super();
        invoker = getInvoker();
    }
    public abstract void open();

}
