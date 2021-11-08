package org.waynezhou.libCamera;

import org.waynezhou.libCamera.eventGroup.CameraCaptureBaseEventGroup;
import org.waynezhou.libUtil.event.BaseEventGroup;

public abstract class CameraCapture extends CameraCaptureBaseEventGroup {
    private final BaseEventGroup<CameraCaptureBaseEventGroup>.Invoker invoker;
    protected CameraCapture(){
        super();
        invoker = getInvoker();
    }
    public abstract void open();

}
