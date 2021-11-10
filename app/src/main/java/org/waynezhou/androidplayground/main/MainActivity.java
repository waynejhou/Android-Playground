package org.waynezhou.androidplayground.main;


import android.content.res.Configuration;

import androidx.annotation.NonNull;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.activity.ComponentizedActivity;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.event.EventListener;
import org.waynezhou.libView.transition.ViewAnimatorArgs;
import org.waynezhou.libView.transition.ViewTransition;

public final class MainActivity extends ComponentizedActivity<MainActivity> implements ILayout, IControl, IRotate, IFocusView {
    private final Layout layout = new Layout();
    private final Rotate rotate = new Rotate(Rotate.AutoRotationSource.System);
    private final FocusView focusView = new FocusView();
    private final MediaMiddle mediaMiddle = new MediaMiddle();
    private final Control control = new Control();
    //final ObjdetTop objdetTop = new ObjdetTop();
    //final MediaTop mediaTop = new MediaTop();
    //final BleControl bleControl = new BleControl();
    //final CameraTop cameraTop = new CameraTop();
    
    @SuppressWarnings("unchecked")
    @Override
    protected ActivityComponent<MainActivity>[] getComponents() {
        return (ActivityComponent<MainActivity>[])new ActivityComponent []{
          layout, rotate, focusView, mediaMiddle, control
        };
    }
    
    @NonNull
    @Override
    public ActivityMainBinding getBinding() {
        return layout.getBinding();
    }
    
    @NonNull
    @Override
    public Layouts getLayouts() {
        return layout.getLayouts();
    }
    
    @Override
    public boolean isContentViewSet() {
        return false;
    }
    
    @Override
    public void setLandCurrent(@NonNull ViewTransition<Layouts> landCurrent) {
    
    }
    
    @Override
    public void setPortCurrent(@NonNull ViewTransition<Layouts> portCurrent) {
    
    }
    
    @NonNull
    @Override
    public ViewTransition<Layouts> getLandCurrent() {
        return layout.getLandCurrent();
    }
    
    @NonNull
    @Override
    public ViewTransition<Layouts> getPortCurrent() {
        return layout.getPortCurrent();
    }
    
    @NonNull
    @Override
    public ViewTransition<Layouts> getCurrent() {
        return layout.getCurrent();
    }
    
    @Override
    public boolean isCurrentLayout(String... tags) {
        return layout.isCurrentLayout(tags);
    }
    
    @Override
    public void changeLayout(@NonNull ViewTransition<Layouts> layout) {
        this.layout.changeLayout(layout);
    }
    
    @Override
    public void animateLayout(
      @NonNull ViewTransition<Layouts> layout,
      @NonNull ViewAnimatorArgs args
    ) {
        this.layout.animateLayout(layout, args);
    }
    
    @Override
    public void setChangedReason(@NonNull LayoutChangedReason changedReason) {
        layout.setChangedReason(changedReason);
    }
    
    @Override
    public void setOnceRotationRequested(@NonNull Runnable run) {
        layout.setOnceRotationRequested(run);
    }
    
    
    @Override
    public void sendControlSignal(@NonNull ControlSignal signal) {
        control.sendControlSignal(signal);
    }
    
    @NonNull
    @Override
    public EventHolder<ControlSignal>.ListenerToken onControlGotSignal(EventListener<ControlSignal> listener) {
        return control.onControlGotSignal(listener);
    }
    
    @Override
    public void rotateActivity(boolean withRequest) {
    
    }
    
    @Override
    public boolean isOrientationChanged(@NonNull Configuration newConfig) {
        return false;
    }
    
    @Override
    public Integer getOrientation() {
        return null;
    }
    
    @Override
    public boolean isOrientationLand() {
        return false;
    }
    
    @Override
    public boolean isOrientationNotLand() {
        return false;
    }
    
    @Override
    public boolean isOrientationPort() {
        return false;
    }
    
    @Override
    public boolean isOrientationNotPort() {
        return false;
    }
    
    @Override
    public EventHolder<Void>.ListenerToken onActivityRotated(@NonNull EventListener<Void> rotated) {
        return rotate.onActivityRotated(rotated);
    }
    
    @Override
    public void focusViewOnNext() {
        focusView.focusViewOnNext();
    }
    
    @Override
    public void focusViewOnPrev() {
        focusView.focusViewOnPrev();
    }
    
    @Override
    public void focusView(@NonNull FocusPosition fPos) {
        focusView.focusView(fPos);
    }
    
    @NonNull
    @Override
    public FocusPosition getFocusViewPos() {
        return focusView.getFocusViewPos();
    }
}
