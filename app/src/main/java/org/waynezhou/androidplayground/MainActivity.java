package org.waynezhou.androidplayground;


import static org.waynezhou.androidplayground.MainActivity.LayoutChangedReason.*;
import static org.waynezhou.libView.view_transition.LayoutTransitionPropertyBridges.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libUtil.PermissionChecker;
import org.waynezhou.libView.view_transition.ViewAnimatorArgs;
import org.waynezhou.libView.view_transition.ViewStep;
import org.waynezhou.libView.view_transition.ViewTransition;
import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.EnumClass;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SensorToggle;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libView.MediaView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // region Input from anything
    private ActivityMainBinding binding;
    private SensorToggle gSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogHelper.i();
        super.onCreate(savedInstanceState);

        LogHelper.i("Set Window Rotation Animation");
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;

        rotate.init();

        LogHelper.i("Set Main Activity Binding");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        LogHelper.i("Set Binding Root GlobalLayoutListener");
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(layout::onGlobalLayoutChanged);
        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(layout::onPreDraw);
        binding.getRoot().getViewTreeObserver().addOnDrawListener(layout::onRootDraw);
        LogHelper.i("Set Content View");
        layout.changedReason = CONTENT_VIEW_SET;
        layout.isContentViewSet = false;
        setContentView(binding.getRoot());

        gSensor = new SensorToggle(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        gSensor.getEventGroup().on(e -> e.changed, this::onGSensorValueChanged);

        PermissionChecker permissionChecker = new PermissionChecker(this, true, Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionChecker.getEventGroup().on(g -> g.permissionGranted, e -> {
            topMedia.create();
            middleMedia.create();
        });
        permissionChecker.fire();
    }

    private void onGSensorValueChanged(SensorChangedEventArgs e) {
        if (!layout.isContentViewSet) return;
        float[] axis = e.event.values;
        final float x = axis[0];
        final float y = axis[1];
        //final float z = axis[2];
        final float g = SensorManager.STANDARD_GRAVITY;
        //LogHelper.d("x: "+x+" y: "+y);
        if ((Math.abs(x - 0) < 1f && Math.abs(y - g) < 1f) && rotate.orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            control.sendSignal(ControlSignal.ROTATE_PORT);
        } else if ((Math.abs(x - g) < 1f && Math.abs(y - 0) < 1f) && rotate.orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogHelper.d(keyCode);
        if (keyCode == KeyEvent.KEYCODE_1) {
            control.sendSignal(ControlSignal.LAYOUT_PORT_STD);

        }
        if (keyCode == KeyEvent.KEYCODE_2) {
            control.sendSignal(ControlSignal.LAYOUT_PORT_TOP_HALF);
        }
        if (keyCode == KeyEvent.KEYCODE_3) {
            control.sendSignal(ControlSignal.LAYOUT_PORT_TOP_FULL);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            control.sendSignal(ControlSignal.MEDIA_PREV_SECTION);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            control.sendSignal(ControlSignal.MEDIA_NEXT_SECTION);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            control.sendSignal(ControlSignal.FOCUS_PREV);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            control.sendSignal(ControlSignal.FOCUS_NEXT);
        }
        return super.onKeyDown(keyCode, event);
    }

    // endregion

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

    private final Layout layout = new Layout();
    private final static ViewAnimatorArgs DefaultAnimatorArgs = ViewAnimatorArgs.builder()
            .setDuration(500)
            .setInterpolator(new DecelerateInterpolator())
            .build();

    private class Layout {

        private void onRootDraw() {
        }

        private boolean onPreDraw() {
            return true;
        }

        @NonNull
        private Runnable onceRotationRequested = DelegateUtils.NothingRunnable;

        public void setOnceRotationRequested(@NonNull Runnable run) {
            onceRotationRequested = run;
        }

        private LayoutChangedReason changedReason = null;

        private boolean isContentViewSet = false;

        private void onGlobalLayoutChanged() {
            if (changedReason == null) return;
            LogHelper.i(changedReason);
            if (changedReason.equals(CONTENT_VIEW_SET)) {
                isContentViewSet = true;
                onContentViewSet();
            } else if (changedReason.equals(LayoutChangedReason.ROTATION_REQUESTED)) {
                onRotationRequested();
                onceRotationRequested.run();
                onceRotationRequested = DelegateUtils.NothingRunnable;
            }
            changedReason = null;
        }

        private void onContentViewSet() {
            refreshLayoutProperties();
            if (rotate.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                layout(landCurrent);

            } else if (rotate.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                layout(portCurrent);
            }
        }

        private void onRotationRequested() {
            refreshLayoutProperties();
            //layoutAuto(ViewAnimatorArgs.builder().setDuration(500).build());
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
            port_topContainer_height = Math.round(shortSideLength * 9f / 16f);
            port_middleContainer_top = rootTop + port_topContainer_height + 50;
            port_middleContainer_height = Math.round(shortSideLength * 9f / 16f);
            port_bottomContainer_top = port_middleContainer_top + port_middleContainer_height + 50;
            port_bottomContainer_height = longSideLength - port_bottomContainer_top;
        }

        private final ViewStep.ViewPreBuild<Layout> topContainer = it -> it
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_topContainer_height).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_topContainer_top).end();

        private final ViewStep.ViewPreBuild<Layout> middleContainer = it -> it
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_middleContainer_top).end();

        private final ViewStep.ViewPreBuild<Layout> bottomContainer = it -> it
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end();

        private final ViewTransition<Layout> land_std = new ViewTransition.Builder<>(this)
                .startAddStep(() -> binding.mainTopContainer)
                .pre(v -> v.setZ(1f))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.longSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> binding.mainMiddleContainer)
                .pre(v -> v.setZ(0.5f))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> h.longSideLength).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> binding.mainBottomContainer)
                .pre(v -> v.setZ(0f))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> h.longSideLength).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> binding.mainFocusView)
                .pre(v -> v.setZ(0))
                .endAddStep()
                .build();

        private
        final ViewTransition<Layout> port_std = new ViewTransition.Builder<>(this)
                /* mainTopContainer */
                .startAddStep(() -> binding.mainTopContainer)
                .pre(v -> v.setZ(1f))
                .preBuild(topContainer)
                .endAddStep()
                /* mainMiddleContainer */
                .startAddStep(() -> binding.mainMiddleContainer)
                .pre(v -> v.setZ(0.5f))
                .preBuild(middleContainer)
                .endAddStep()
                /* mainBottomContainer */
                .startAddStep(() -> binding.mainBottomContainer)
                .pre(v -> v.setZ(0f))
                .preBuild(bottomContainer)
                .endAddStep()
                /* mainFocusView */
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_TOP, () -> binding.mainFocusView)
                .pre(v -> v.setZ(1))
                .preBuild(topContainer)
                .endAddStep()
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_MIDDLE, () -> binding.mainFocusView)
                .pre(v -> v.setZ(1))
                .preBuild(middleContainer)
                .endAddStep()
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_BOTTOM, () -> binding.mainFocusView)
                .pre(v -> v.setZ(1))
                .preBuild(bottomContainer)
                .endAddStep()
                .build();

        private final ViewTransition<Layout> port_topFull = new ViewTransition.Builder<>(this)
                .startAddStep(() -> binding.mainTopContainer)
                .pre(v -> v.setZ(1f))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.longSideLength).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> binding.mainMiddleContainer)
                .pre(v -> v.setZ(0.5f))
                .preBuild(middleContainer)
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end()
                .endAddStep()
                .startAddStep(() -> binding.mainBottomContainer)
                .pre(v -> v.setZ(0f))
                .preBuild(bottomContainer)
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end()
                .endAddStep()
                /* mainFocusView */
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_TOP, () -> binding.mainFocusView)
                .pre(v -> v.setZ(1))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.longSideLength).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_MIDDLE, () -> binding.mainFocusView)
                .pre(v -> v.setZ(0))
                .endAddStep()
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_BOTTOM, () -> binding.mainFocusView)
                .pre(v -> v.setZ(0))
                .endAddStep()
                .build();

        private final ViewTransition<Layout> port_topHalf = new ViewTransition.Builder<>(this)
                .startAddStep(() -> binding.mainTopContainer)
                .pre(v -> v.setZ(1f))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> binding.mainMiddleContainer)
                .pre(v -> v.setZ(0f))
                .preBuild(middleContainer)
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end()
                .endAddStep()
                .startAddStep(() -> binding.mainBottomContainer)
                .pre(v -> v.setZ(1f))
                .preBuild(bottomContainer)
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
                .endAddStep()
                /* mainFocusView */
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_TOP, () -> binding.mainFocusView)
                .pre(v -> v.setZ(1))
                .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
                .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
                .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
                .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end()
                .endAddStep()
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_MIDDLE, () -> binding.mainFocusView)
                .pre(v -> v.setZ(0))
                .endAddStep()
                .startAddStep(() -> focusView.focusPos == FocusPositions.FOCUS_BOTTOM, () -> binding.mainFocusView)
                .pre(v -> v.setZ(1f))
                .preBuild(bottomContainer)
                .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
                .endAddStep()
                .build();

        private ViewTransition<Layout> landCurrent = land_std;
        private ViewTransition<Layout> portCurrent = port_std;

        private ViewTransition<Layout> getCurrent() {
            if (rotate.isPort()) {
                return portCurrent;
            }
            return landCurrent;
        }

        private void layout(ViewTransition<Layout> transition) {
            transition.runWithoutAnimation(true);
        }

        private void layoutAnimated(ViewTransition<Layout> transition) {
            layoutAnimated(transition, DefaultAnimatorArgs);
        }

        private void layoutAnimated(ViewTransition<Layout> transition, ViewAnimatorArgs args) {
            transition.createAnimatorSet(args).start();
        }
    }

    static class LayoutChangedReason extends EnumClass.Int {
        public static final LayoutChangedReason ROTATION_REQUESTED = new LayoutChangedReason("Rotation Requested");
        public static final LayoutChangedReason CONTENT_VIEW_SET = new LayoutChangedReason("Content View Set");

        private LayoutChangedReason(String identifier) {
            super(identifier);
        }
    }

    private final Control control = new Control();

    private static class ControlSignal extends EnumClass.Int {
        public static final ControlSignal ROTATE = new ControlSignal("Rotate");
        public static final ControlSignal ROTATE_PORT = new ControlSignal("Rotate Port");
        public static final ControlSignal ROTATE_LAND = new ControlSignal("Rotate Land");
        public static final ControlSignal LAYOUT_PORT_TOP_FULL = new ControlSignal("Layout Port Top Full");
        public static final ControlSignal LAYOUT_PORT_STD = new ControlSignal("Layout Port Std");
        public static final ControlSignal LAYOUT_PORT_TOP_HALF = new ControlSignal("Layout Top Half");
        public static final ControlSignal MEDIA_NEXT_SECTION = new ControlSignal("Media Next Section");
        public static final ControlSignal MEDIA_PREV_SECTION = new ControlSignal("Media Prev Section");
        public static final ControlSignal FOCUS_NEXT = new ControlSignal("Focus Next");
        public static final ControlSignal FOCUS_PREV = new ControlSignal("Focus Prev");

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
            } else if (ControlSignal.LAYOUT_PORT_STD.equals(signal)) {
                if (rotate.isPort()) {
                    layout.portCurrent = layout.port_std;
                    layout.layoutAnimated(layout.port_std);
                }
            } else if (ControlSignal.LAYOUT_PORT_TOP_HALF.equals(signal)) {
                if (rotate.isPort()) {
                    if(focusView.focusPos == FocusPositions.FOCUS_MIDDLE){
                        focusView.focus(FocusPositions.FOCUS_TOP);
                    }
                    layout.portCurrent = layout.port_topHalf;
                    layout.layoutAnimated(layout.port_topHalf);
                }
            } else if (ControlSignal.LAYOUT_PORT_TOP_FULL.equals(signal)) {
                if (rotate.isPort()) {
                    if(focusView.focusPos != FocusPositions.FOCUS_TOP){
                        focusView.focus(FocusPositions.FOCUS_TOP);
                    }
                    layout.portCurrent = layout.port_topFull;
                    layout.layoutAnimated(layout.port_topFull);
                }
            } else if (ControlSignal.MEDIA_NEXT_SECTION.equals(signal)) {
                if(focusView.focusPos == FocusPositions.FOCUS_TOP){
                    topMedia.toNextSection();
                }else if(focusView.focusPos == FocusPositions.FOCUS_MIDDLE){
                    middleMedia.toNextSection();
                }
            } else if (ControlSignal.MEDIA_PREV_SECTION.equals(signal)) {
                if(focusView.focusPos == FocusPositions.FOCUS_TOP){
                    topMedia.toPrevSection();
                }else if(focusView.focusPos == FocusPositions.FOCUS_MIDDLE){
                    middleMedia.toPrevSection();
                }
            } else if (ControlSignal.FOCUS_NEXT.equals(signal)) {
                focusView.focusNext();
            } else if (ControlSignal.FOCUS_PREV.equals(signal)) {
                focusView.focusPrev();
            }
        }
    }

    private final Rotate rotate = new Rotate();

    private class Rotate {
        private Integer orientation = null;

        private void init() {
            orientation = getResources().getConfiguration().orientation;
        }

        private boolean isLand() {
            return orientation != null && orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }

        private boolean isPort() {
            return orientation != null && orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        private void auto() {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                _toPort();
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                _toLand();
            }
        }

        private void toLand() {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return;
            }
            _toLand();
        }

        private void toPort() {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                return;
            }
            _toPort();
        }

        private void _toLand() {
            LogHelper.i();
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            layout.portCurrent.transpose().runWithoutAnimation(false);
            layout.onceRotationRequested = () -> {
                layout.layoutAnimated(layout.landCurrent);
            };
            layout.changedReason = LayoutChangedReason.ROTATION_REQUESTED;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @SuppressLint("SourceLockedOrientationActivity")
        private void _toPort() {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            layout.landCurrent.transpose().runWithoutAnimation(false);
            layout.onceRotationRequested = () -> {
                layout.layoutAnimated(layout.portCurrent);
            };
            layout.changedReason = LayoutChangedReason.ROTATION_REQUESTED;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private TopMedia topMedia = new TopMedia();

    private class TopMedia {
        private MediaView view;
        private final MediaView.Section[] sections = new MediaView.Section[]{
                new MediaView.Section(30, 0, 1000, true),
                new MediaView.Section(30, 1000, 2000, true),
                new MediaView.Section(30, 2000, 3000, true),
                new MediaView.Section(30, 3000, 4000, true),
                new MediaView.Section(30, 4000, 5000, true),
                new MediaView.Section(30, 5000, 6000, true),
                new MediaView.Section(30, 6000, 7000, true),
                new MediaView.Section(30, 7000, 8000, true),
        };
        private int sectionIdx = 0;

        private void create() {
            view = new MediaView(MainActivity.this);
            binding.mainTopContainer.addView(view);
            try {
                view.configPrepareVideo(Environment.getExternalStorageDirectory() + "/DCIM/dummy video.mp4")
                        .setOnPrepared(() -> {
                            view.setOnceVideoSeekComplete(() -> {
                                view.player.start();
                            });
                            view.setSection(sections[sectionIdx]);
                        })
                        .setLooping(true)
                        .prepare();
            } catch (IOException ex) {
                LogHelper.e(ex);
            }
        }

        private void toNextSection() {
            if (view.isPlayerPrepared()) {
                sectionIdx = (sectionIdx + 1) % sections.length;
                view.setSection(sections[sectionIdx]);
            }
        }

        private void toPrevSection() {
            if (view.isPlayerPrepared()) {
                sectionIdx = (sections.length + sectionIdx - 1) % sections.length;
                view.setSection(sections[sectionIdx]);
            }
        }
    }

    private MiddleMedia middleMedia = new MiddleMedia();

    private class MiddleMedia {
        private MediaView view;
        private final MediaView.Section[] sections = new MediaView.Section[]{
                new MediaView.Section(30, 0, 1000, true),
                new MediaView.Section(30, 1000, 2000, true),
                new MediaView.Section(30, 2000, 3000, true),
                new MediaView.Section(30, 3000, 4000, true),
                new MediaView.Section(30, 4000, 5000, true),
                new MediaView.Section(30, 5000, 6000, true),
                new MediaView.Section(30, 6000, 7000, true),
                new MediaView.Section(30, 7000, 8000, true),
        };
        private int sectionIdx = 0;

        private void create() {
            view = new MediaView(MainActivity.this);
            binding.mainMiddleContainer.addView(view);
            try {
                view.configPrepareVideo(Environment.getExternalStorageDirectory() + "/DCIM/dummy video port.mp4")
                        .setOnPrepared(() -> {
                            view.setOnceVideoSeekComplete(() -> {
                                view.player.start();
                            });
                            view.setSection(sections[sectionIdx]);
                        })
                        .setLooping(true)
                        .prepare();
            } catch (IOException ex) {
                LogHelper.e(ex);
            }
        }

        private void toNextSection() {
            if (view.isPlayerPrepared()) {
                sectionIdx = (sectionIdx + 1) % sections.length;
                view.setSection(sections[sectionIdx]);
            }
        }

        private void toPrevSection() {
            if (view.isPlayerPrepared()) {
                sectionIdx = (sections.length + sectionIdx - 1) % sections.length;
                view.setSection(sections[sectionIdx]);
            }
        }
    }

    private final FocusView focusView = new FocusView();

    private class FocusView {
        private FocusPositions focusPos = FocusPositions.FOCUS_TOP;

        private void focusNext() {
            if(rotate.isLand()) return;
            if(rotate.isPort() && layout.portCurrent == layout.port_topFull) return;
            FocusPositions nextPos = (FocusPositions) FocusPositions.getMap().get(Math.min(Math.max(focusPos.identifier + 1, 0), 2));
            if(rotate.isPort() && layout.portCurrent == layout.port_topHalf &&
                    nextPos == FocusPositions.FOCUS_MIDDLE
            ){
                nextPos = FocusPositions.FOCUS_BOTTOM;
            }
            focus(nextPos);
        }

        private void focusPrev() {
            if(rotate.isLand()) return;
            if(rotate.isPort() && layout.portCurrent == layout.port_topFull) return;
            FocusPositions nextPos = (FocusPositions) FocusPositions.getMap().get(Math.min(Math.max(focusPos.identifier - 1, 0), 2));
            if(rotate.isPort() && layout.portCurrent == layout.port_topHalf &&
                    nextPos == FocusPositions.FOCUS_MIDDLE
            ){
                nextPos = FocusPositions.FOCUS_TOP;
            }
            focus(nextPos);
        }

        private void focus(FocusPositions fPos) {
            focusPos = fPos;
            layout.layoutAnimated(layout.getCurrent());
        }
    }

    private static class FocusPositions extends EnumClass.Int {
        private static final FocusPositions FOCUS_TOP = new FocusPositions(0, "Focus Top");
        private static final FocusPositions FOCUS_MIDDLE = new FocusPositions(1, "Focus Middle");
        private static final FocusPositions FOCUS_BOTTOM = new FocusPositions(2, "Focus Bottom");

        protected FocusPositions(int identifier, String statement) {
            super(identifier, statement);
        }
    }
}
