package org.waynezhou.androidplayground;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.androidplayground.layout.LayoutManager;
import org.waynezhou.androidplayground.view_transition.LayoutTransitionPropertyBridges;
import org.waynezhou.androidplayground.view_transition.LayoutTransitionSteps;
import org.waynezhou.androidplayground.view_transition.ValueGetter;
import org.waynezhou.androidplayground.view_transition.ViewAnimatorArgs;
import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.EnumClass;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SensorToggle;
import org.waynezhou.libUtil.StandardKt;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // region Input from anything
    private ActivityMainBinding binding;
    private LayoutManager<MainActivity> layoutManager;
    private SensorToggle gSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogHelper.i();
        super.onCreate(savedInstanceState);

        LogHelper.i("Set Window Rotation Animation");
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;

        orientation = getResources().getConfiguration().orientation;

        LogHelper.i("Set Main Activity Binding");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        LogHelper.i("Set Binding Root GlobalLayoutListener");
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(this::globalLayoutChanged);
        LogHelper.i("Set Content View");
        globalLayoutChangedReason = LayoutChangedReason.CONTENT_VIEW_SET;
        setContentView(binding.getRoot());

        layoutManager = new LayoutManager<>(this);

        gSensor = new SensorToggle(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        gSensor.getEventGroup().on(e -> e.changed, this::onGSensorValueChanged);
    }

    private void onGSensorValueChanged(SensorChangedEventArgs e) {
        float[] axis = e.event.values;
        final float x = axis[0];
        final float y = axis[1];
        //final float z = axis[2];
        final float g = SensorManager.STANDARD_GRAVITY;
        //LogHelper.d("x: "+x+" y: "+y);
        if ((Math.abs(x - 0) < 1f && Math.abs(y - g) < 1f) && orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                /*this.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                nextLayoutChanged = () -> {
                    this.nextLayoutChanged = DelegateUtils.NothingRunnable;
                    layoutManager.openTemplate(portrait).setArgs(LayoutExecutionArgs.builder().setAnimationDuration(1000).build()).execute();
                };
                layoutManager.openTemplate(pre_portrait).executeWithoutRequest();*/
            control.sendSignal(ControlSignal.ROTATE_PORT);
        } else if ((Math.abs(x - g) < 1f && Math.abs(y - 0) < 1f) && orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                /*this.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                nextLayoutChanged = () -> {
                    this.nextLayoutChanged = DelegateUtils.NothingRunnable;
                    layoutManager.openTemplate(landscape).setArgs(LayoutExecutionArgs.builder().setAnimationDuration(1000).build()).execute();
                };
                layoutManager.openTemplate(pre_landscape).executeWithoutRequest();*/
            control.sendSignal(ControlSignal.ROTATE_LAND);
        }
    }

    @Override
    protected void onDestroy() {
        LogHelper.i();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        LogHelper.i();
        super.onResume();
        gSensor.On();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        LogHelper.i();
        super.onPause();
        gSensor.Off();
    }

    @Override
    protected void onStart() {
        LogHelper.i();
        super.onStart();
    }

    @Override
    protected void onStop() {
        LogHelper.i();
        super.onStop();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        LogHelper.i();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        LogHelper.i();
        super.onPostResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    // endregion

    // region layout variables
    private static class LayoutChangedReason extends EnumClass.Int {
        public static final LayoutChangedReason ROTATION_REQUESTED = new LayoutChangedReason("Rotation Requested");
        public static final LayoutChangedReason CONTENT_VIEW_SET = new LayoutChangedReason("Content View Set");

        private LayoutChangedReason(String identifier) {
            super(identifier);
        }
    }

    @NonNull
    private Runnable onceRotationRequested = DelegateUtils.NothingRunnable;
    private volatile LayoutChangedReason globalLayoutChangedReason = null;

    private void globalLayoutChanged() {
        if (globalLayoutChangedReason == null) return;
        LogHelper.i(globalLayoutChangedReason);
        if (globalLayoutChangedReason.equals(LayoutChangedReason.CONTENT_VIEW_SET)) {
            onContentViewSet();
        } else if (globalLayoutChangedReason.equals(LayoutChangedReason.ROTATION_REQUESTED)) {
            onceRotationRequested.run();
            onceRotationRequested = DelegateUtils.NothingRunnable;
            onRotationRequested();
        }
        globalLayoutChangedReason = null;
    }

    private void onContentViewSet() {
        refreshLayoutProperties();
        layoutAuto();
    }

    private void onRotationRequested() {
        refreshLayoutProperties();
        layoutAuto();
    }

    private Integer rootWidth = null;
    private Integer rootHeight = null;
    private Integer rootLeft = null;
    private Integer rootTop = null;
    private Integer longSideLength = null;
    private Integer shortSideLength = null;

    private Integer port_topContainer_height = null;
    private Integer port_topContainer_top = null;
    private Integer port_middleContainer_top = null;
    private Integer port_middleContainer_height = null;
    private Integer port_bottomContainer_top = null;
    private Integer port_bottomContainer_height = null;

    private void refreshLayoutProperties() {
        final int width = binding.getRoot().getWidth();
        final int height = binding.getRoot().getHeight();
        LogHelper.d("globalLayoutChanged root: [Width: %d, Height: %d]", width, height);
        rootWidth = width;
        rootHeight = height;
        longSideLength = Math.max(rootWidth, rootHeight);
        shortSideLength = Math.min(rootWidth, rootHeight);
        rootLeft = 0;
        rootTop = 0;
        port_topContainer_top = rootTop;
        port_topContainer_height = (int) (shortSideLength * 9f / 16f);
        port_middleContainer_top = rootTop + port_topContainer_height + 50;
        port_middleContainer_height = (int) (shortSideLength * 9f / 16f);
        port_bottomContainer_top = port_middleContainer_top + port_middleContainer_height + 50;
        port_bottomContainer_height = longSideLength - port_bottomContainer_top;
    }

    // endregion

    private void layoutAuto(){
        if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            //layoutToLand();
        }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            layoutToPort();
        }
    }
    private void layoutToPort() {
        LogHelper.i();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                new LayoutTransitionSteps.ViewStep(
                        new HashMap<String, ValueGetter[]>(){{
                            put(LayoutTransitionPropertyBridges.PROP_WIDTH, ValueGetter.prop(LayoutTransitionPropertyBridges.PROP_WIDTH)
                                    .FromCurrentLayoutTo(new ValueGetter.FromValueHolder<MainActivity>(h->h.shortSideLength)));
                        }}
                )
                        .generateAnimator(
                                binding.mainTopContainer, this
                                , StandardKt.apply(new ViewAnimatorArgs.Builder(),it->{
                                    it.setDuration(500);
                                }).build()
                        )
        );
        set.start();
    }

    private final Control control = new Control();

    private static class ControlSignal extends EnumClass.Int {
        public static final ControlSignal ROTATE = new ControlSignal("Rotate");
        public static final ControlSignal ROTATE_PORT = new ControlSignal("Rotate Port");
        public static final ControlSignal ROTATE_LAND = new ControlSignal("Rotate Land");

        protected ControlSignal(String statement) {
            super(statement);
        }
    }

    private class Control {
        public void sendSignal(ControlSignal signal) {
            LogHelper.i(signal);
            if (ControlSignal.ROTATE.equals(signal)) {
                rotate.auto();
            } else if (ControlSignal.ROTATE_LAND.equals(signal)) {
                rotate.toLand();
            } else if (ControlSignal.ROTATE_PORT.equals(signal)) {
                rotate.toPort();
            }
        }
    }

    private volatile Integer orientation = null;
    private final Rotate rotate = new Rotate();

    private class Rotate {
        public void auto() {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                _toPort();
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                _toLand();
            }
        }

        public void toLand() {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return;
            }
            _toLand();
        }

        public void toPort() {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                return;
            }
            _toPort();
        }

        private void _toLand() {
            LogHelper.i();
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            globalLayoutChangedReason = LayoutChangedReason.ROTATION_REQUESTED;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @SuppressLint("SourceLockedOrientationActivity")
        private void _toPort() {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            globalLayoutChangedReason = LayoutChangedReason.ROTATION_REQUESTED;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = //View.SYSTEM_UI_FLAG_LOW_PROFILE
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}
