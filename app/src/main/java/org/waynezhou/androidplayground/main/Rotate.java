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

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.event.EventHandler;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.event.EventListener;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.toggle.SensorToggle;

interface IRotate {
    void rotateActivity(boolean withRequest);
    
    boolean isOrientationChanged(@NonNull Configuration newConfig);
    
    Integer getOrientation();
    
    boolean isOrientationLand();
    
    boolean isOrientationNotLand();
    
    boolean isOrientationPort();
    
    boolean isOrientationNotPort();
    EventHolder<Void>.ListenerToken onActivityRotated(@NonNull EventListener<Void> rotated);
}

final class Rotate extends ActivityComponent<MainActivity> implements IRotate {
    private Integer orientation = null;
    private final AutoRotationSource autoRotate;
    @SuppressWarnings("FieldCanBeLocal")
    private SensorToggle gSensor;
    
    Rotate(AutoRotationSource autoRotate) {
        this.autoRotate = autoRotate;
    }
    
    @Override
    protected void onInit() {
        orientation = host.getResources().getConfiguration().orientation;
        this.autoRotate.init(this);
    }
    
    @Override
    protected void onHostCreate(Bundle bundle) {
        host.onControlGotSignal(this::onControlGotSignal);
        autoRotate.create();
    }
    
    // region create
    private void createGSensor() {
        gSensor = new SensorToggle(host, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        gSensor.getEvents().on(e -> e.changed, this::onGSensorValueChanged);
    }
    
    // endregion
    
    // region input route
    private void onHostConfigurationChanged(Configuration newConfig) {
        if (isOrientationChanged(newConfig)) {
            host.sendControlSignal(CTRL_ROTATE);
        }
    }
    
    private void onGSensorValueChanged(SensorChangedEventArgs e) {
        if (!host.isContentViewSet()) return;
        float[] axis = e.event.values;
        final float x = axis[0];
        final float y = axis[1];
        //final float z = axis[2];
        final float g = SensorManager.STANDARD_GRAVITY;
        //LogHelper.d("x: "+x+" y: "+y);
        if ((Math.abs(x - 0) < 1f && Math.abs(y - g) < 1f) && isOrientationNotPort()) {
            host.sendControlSignal(CTRL_ROTATE_PORT_REQUEST);
        } else if ((Math.abs(x - g) < 1f && Math.abs(y - 0) < 1f) && isOrientationNotLand()) {
            host.sendControlSignal(CTRL_ROTATE_LAND_REQUEST);
        }
    }
    // endregion
    
    // region get set property
    @Override
    public boolean isOrientationChanged(@NonNull Configuration newConfig) {
        return !this.orientation.equals(newConfig.orientation);
    }
    
    @Override
    public Integer getOrientation() {
        return orientation;
    }
    
    @Override
    public boolean isOrientationLand() {
        return orientation != null && orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    @Override
    public boolean isOrientationPort() {
        return orientation != null && orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    @Override
    public boolean isOrientationNotLand() {
        return orientation == null || orientation != Configuration.ORIENTATION_LANDSCAPE;
    }
    
    @Override
    public boolean isOrientationNotPort() {
        return orientation == null || orientation != Configuration.ORIENTATION_PORTRAIT;
    }
    // endregion
    
    // region output action
    private void onControlGotSignal(ControlSignal signal) {
        if (CTRL_ROTATE.equals(signal)) {
            rotateActivity(false);
        } else if (CTRL_ROTATE_REQUEST.equals(signal)) {
            rotateActivity(true);
        } else if (CTRL_ROTATE_LAND_REQUEST.equals(signal)) {
            toLand(true);
        } else if (CTRL_ROTATE_PORT_REQUEST.equals(signal)) {
            toPort(true);
        }
    }
    
    
    @Override
    public void rotateActivity(boolean withRequest) {
        if (isOrientationLand()) {
            _toPort(withRequest);
        } else if (isOrientationPort()) {
            _toLand(withRequest);
        }
    }
    
    private void to(
      int orientation,
      boolean withRequest
    ) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            _toLand(withRequest);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            _toPort(withRequest);
        }
    }
    
    private void toLand(boolean withRequest) {
        if (isOrientationLand()) {
            return;
        }
        _toLand(withRequest);
    }
    
    private void toPort(boolean withRequest) {
        if (isOrientationPort()) {
            return;
        }
        _toPort(withRequest);
    }
    
    private void _toLand(boolean withRequest) {
        LogHelper.i();
        orientation = Configuration.ORIENTATION_LANDSCAPE;
        host.getPortCurrent().transpose().runWithoutAnimation(false);
        host.setOnceRotationRequested(() -> {
            host.animateLayout(host.getLandCurrent(), Layout.DefaultAnimatorArgs);
        });
        host.setChangedReason(ROTATION_REQUESTED);
        host.runOnUiThread(() -> rotated.invoke(null));
        if (withRequest) host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    @SuppressLint("SourceLockedOrientationActivity")
    private void _toPort(boolean withRequest) {
        orientation = Configuration.ORIENTATION_PORTRAIT;
        host.getLandCurrent().transpose().runWithoutAnimation(false);
        host.setOnceRotationRequested(() -> {
            host.animateLayout(host.getPortCurrent(), Layout.DefaultAnimatorArgs);
        });
        host.setChangedReason(ROTATION_REQUESTED);
        host.runOnUiThread(() -> rotated.invoke(null));
        if (withRequest) host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    // endregion
    
    // region events
    private final EventHandler<Void> rotated = new EventHandler<>();
    
    @NonNull
    @Override
    public EventHolder<Void>.ListenerToken onActivityRotated(@NonNull EventListener<Void> rotated) {
        return this.rotated.on(rotated);
    }
    // endregion
    
    static final class AutoRotationSource {
        final static AutoRotationSource None = new AutoRotationSource($ -> {
        });
        final static AutoRotationSource GSensor = new AutoRotationSource(Rotate::createGSensor);
        final static AutoRotationSource System = new AutoRotationSource(rotate -> {
            rotate.host.getEvents().on(g -> g.configurationChanged, newConfig -> {
                if (rotate.isOrientationChanged(newConfig)) {
                    rotate.host.sendControlSignal(CTRL_ROTATE);
                }
            });
        });
        
        private Rotate rotate;
        private final Consumer<Rotate> create;
        
        private AutoRotationSource(Consumer<Rotate> create) {
            this.create = create;
        }
        
        void init(Rotate rotate) {
            this.rotate = rotate;
        }
        
        void create() {
            create.accept(rotate);
        }
    }
}
