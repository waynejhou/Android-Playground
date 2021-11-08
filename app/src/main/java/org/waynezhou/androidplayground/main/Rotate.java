package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_ROTATE;
import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_ROTATE_LAND_REQUEST;
import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_ROTATE_PORT_REQUEST;
import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_ROTATE_REQUEST;
import static org.waynezhou.androidplayground.main.LayoutChangedReason.*;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.core.util.Consumer;

import org.waynezhou.libUtil.event.EventHandler;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.event.EventListener;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.toggle.SensorToggle;

final class Rotate {
    private Activity host;
    private Integer orientation = null;
    private Layout layout;
    private Control control;
    private AutoRotationSource autoRotate;
    @SuppressWarnings("FieldCanBeLocal")
    private SensorToggle gSensor;
    void init(Activity activity, @SuppressWarnings("SameParameterValue") AutoRotationSource autoRotate) {
        host = activity;
        layout = host.layout;
        control = host.control;
        orientation = host.getResources().getConfiguration().orientation;
        this.autoRotate = autoRotate;
        this.autoRotate.init(this);
        host.getEvents()
          .on(h->h.create, this::onHostCreate);
    }
    
    private void onHostCreate(Bundle bundle) {
        control.onGotSignal(this::onControlGotSignal);
        autoRotate.create();
    }
    
    // region create
    private void createGSensor(){
        gSensor = new SensorToggle(host, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        gSensor.getEvents().on(e -> e.changed, this::onGSensorValueChanged);
    }

    // endregion
    
    // region input route
    private void onHostConfigurationChanged(Configuration newConfig) {
        if (isOrientationChanged(newConfig)) {
            control.sendSignal(CTRL_ROTATE);
        }
    }
    private void onGSensorValueChanged(SensorChangedEventArgs e) {
        if (!layout.isContentViewSet()) return;
        float[] axis = e.event.values;
        final float x = axis[0];
        final float y = axis[1];
        //final float z = axis[2];
        final float g = SensorManager.STANDARD_GRAVITY;
        //LogHelper.d("x: "+x+" y: "+y);
        if ((Math.abs(x - 0) < 1f && Math.abs(y - g) < 1f) && isNotPort()) {
            control.sendSignal(CTRL_ROTATE_PORT_REQUEST);
        } else if ((Math.abs(x - g) < 1f && Math.abs(y - 0) < 1f) && isNotLand()) {
            control.sendSignal(CTRL_ROTATE_LAND_REQUEST);
        }
    }
    // endregion
    
    // region get set property
    boolean isOrientationChanged(Configuration newConfig){
        return !this.orientation.equals(newConfig.orientation);
    }
    Integer getOrientation() {
        return orientation;
    }
    boolean isLand() {
        return orientation != null && orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    boolean isPort() {
        return orientation != null && orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    boolean isNotLand() {
        return orientation == null || orientation != Configuration.ORIENTATION_LANDSCAPE;
    }
    
    boolean isNotPort() {
        return orientation == null || orientation != Configuration.ORIENTATION_PORTRAIT;
    }
    // endregion
    
    // region output action
    private void onControlGotSignal(ControlSignal signal) {
        if(CTRL_ROTATE.equals(signal)){
            auto(false);
        }else if(CTRL_ROTATE_REQUEST.equals(signal)){
            auto(true);
        }else if(CTRL_ROTATE_LAND_REQUEST.equals(signal)){
            toLand(true);
        }else if(CTRL_ROTATE_PORT_REQUEST.equals(signal)){
            toPort(true);
        }
    }
    
    
    void auto(boolean withRequest) {
        if (isLand()) {
            _toPort(withRequest);
        } else if (isPort()) {
            _toLand(withRequest);
        }
    }
    
    private void to(int orientation, boolean withRequest) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            _toLand(withRequest);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            _toPort(withRequest);
        }
    }
    
    private void toLand(boolean withRequest) {
        if (isLand()) {
            return;
        }
        _toLand(withRequest);
    }
    
    private void toPort(boolean withRequest) {
        if (isPort()) {
            return;
        }
        _toPort(withRequest);
    }
    
    private void _toLand(boolean withRequest) {
        LogHelper.i();
        orientation = Configuration.ORIENTATION_LANDSCAPE;
        layout.getPortCurrent().transpose().runWithoutAnimation(false);
        layout.setOnceRotationRequested(() -> {
            layout.layoutAnimated(layout.getLandCurrent());
        });
        layout.setChangedReason(ROTATION_REQUESTED);
        host.runOnUiThread(()->rotated.invoke(null));
        if(withRequest) host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    @SuppressLint("SourceLockedOrientationActivity")
    private void _toPort(boolean withRequest) {
        orientation = Configuration.ORIENTATION_PORTRAIT;
        layout.getLandCurrent().transpose().runWithoutAnimation(false);
        layout.setOnceRotationRequested(() -> {
            layout.layoutAnimated(layout.getPortCurrent());
        });
        layout.setChangedReason(ROTATION_REQUESTED);
        host.runOnUiThread(()->rotated.invoke(null));
        if(withRequest) host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    // endregion
    
    // region events
    private final EventHandler<Void> rotated = new EventHandler<>();
    
    public EventHolder<Void>.ListenerToken onRotated(EventListener<Void> rotated) {
        return this.rotated.on(rotated);
    }
    // endregion
    
    static final class AutoRotationSource{
        final static AutoRotationSource None = new AutoRotationSource($->{});
        final static AutoRotationSource GSensor = new AutoRotationSource(Rotate::createGSensor);
        final static AutoRotationSource System = new AutoRotationSource(rotate->{
            rotate.host.getEvents().on(g->g.configurationChanged, newConfig->{
                if(rotate.isOrientationChanged(newConfig)){
                    rotate.control.sendSignal(CTRL_ROTATE);
                }
            });
        });
        
        private Rotate rotate;
        private final Consumer<Rotate> create;
        private AutoRotationSource(Consumer<Rotate> create){
            this.create = create;
        }
        void init(Rotate rotate){
            this.rotate = rotate;
        }
    
        void create() {
            create.accept(rotate);
        }
    }
}
