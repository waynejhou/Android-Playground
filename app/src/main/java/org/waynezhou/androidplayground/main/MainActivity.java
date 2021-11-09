package org.waynezhou.androidplayground.main;


import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.activity.ComponentizedActivity;
import org.waynezhou.libView.transition.ViewAnimatorArgs;
import org.waynezhou.libView.transition.ViewTransition;

public final class MainActivity extends ComponentizedActivity<MainActivity> implements ILayout, IControl {
    private final Layout layout = new Layout();
    private final Rotate rotate = new Rotate(Rotate.AutoRotationSource.System);
    private final FocusView focusView = new FocusView();
    private final MediaMiddle mediaMiddle = new MediaMiddle();
    private final Control control = new Control();
    //final ObjdetTop objdetTop = new ObjdetTop();
    //final MediaTop mediaTop = new MediaTop();
    //final BleControl bleControl = new BleControl();
    //final CameraTop cameraTop = new CameraTop();
    
    @Override
    protected ActivityComponent<MainActivity>[] getComponents() {
        return new ActivityComponent<MainActivity>[]{
            layout, rotate, focusView, mediaMiddle, control
        };
    }
    
    @Override
    public ActivityMainBinding getBinding() {
        return layout.getBinding();
    }
    
    @Override
    public Layouts getLayouts() {
        return layout.getLayouts();
    }
    
    @Override
    public boolean isContentViewSet() {
        return false;
    }
    
    @Override
    public void setLandCurrent(ViewTransition<Layouts> landCurrent) {
    
    }
    
    @Override
    public void setPortCurrent(ViewTransition<Layouts> portCurrent) {
    
    }
    
    @Override
    public ViewTransition<Layouts> getLandCurrent() {
        return null;
    }
    
    @Override
    public ViewTransition<Layouts> getPortCurrent() {
        return null;
    }
    
    @Override
    public ViewTransition<Layouts> getCurrent() {
        return null;
    }
    
    @Override
    public boolean isCurrentLayout(String... tags) {
        return layout.isCurrentLayout(tags);
    }
    
    @Override
    public void changeLayout(ViewTransition<Layouts> layout) {
        this.layout.changeLayout(layout);
    }
    
    @Override
    public void animateLayout(
      ViewTransition<Layouts> layout,
      ViewAnimatorArgs args
    ) {
        this.layout.animateLayout(layout, args);
    }
    
    @Override
    public void sendControlSignal(ControlSignal signal) {
        control.sendControlSignal(signal);
    }
}
