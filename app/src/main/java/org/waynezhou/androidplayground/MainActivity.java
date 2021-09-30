package org.waynezhou.androidplayground;

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
import org.waynezhou.androidplayground.layout.LayoutDestination;
import org.waynezhou.androidplayground.layout.LayoutExecutionArgs;
import org.waynezhou.androidplayground.layout.LayoutManager;
import org.waynezhou.androidplayground.layout.LayoutTemplate;
import org.waynezhou.androidplayground.layout.ViewLayoutVariables;
import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SensorToggle;
import org.waynezhou.libUtil.StandardKt;

import static org.waynezhou.androidplayground.layout.LayoutProperties.*;

public class MainActivity extends AppCompatActivity {

    // region activity callback
    private ActivityMainBinding binding;
    private LayoutManager<MainActivity> layoutManager;
    private SensorToggle gSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(this::globalLayoutChanged);
        setContentView(binding.getRoot());
        initLayoutVariables();
        layoutManager = new LayoutManager<>(this);

        gSensor = new SensorToggle(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        gSensor.getEventGroup().on(e -> e.changed, e -> {
            float[] axis = e.event.values;
            final float x = axis[0];
            final float y = axis[1];
            //final float z = axis[2];
            final float g = SensorManager.STANDARD_GRAVITY;
            //LogHelper.d("x: "+x+" y: "+y);
            if ((Math.abs(x - 0) < 1f && Math.abs(y - g) < 1f) && this.orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                this.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                nextLayoutChanged = () -> {
                    this.nextLayoutChanged = DelegateUtils.NothingRunnable;
                    layoutManager.openTemplate(portrait).setArgs(LayoutExecutionArgs.builder().setAnimationDuration(1000).build()).execute();
                };
                layoutManager.openTemplate(pre_portrait).executeWithoutRequest();
                rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if ((Math.abs(x - g) < 1f && Math.abs(y - 0) < 1f) && this.orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                this.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                nextLayoutChanged = () -> {
                    this.nextLayoutChanged = DelegateUtils.NothingRunnable;
                    layoutManager.openTemplate(landscape).setArgs(LayoutExecutionArgs.builder().setAnimationDuration(1000).build()).execute();
                };
                layoutManager.openTemplate(pre_landscape).executeWithoutRequest();
                rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gSensor.On();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gSensor.Off();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    // endregion

    // region rotate
    private void rotate(int screenOrientation) {
        if (orientation == screenOrientation) return;
        orientation = screenOrientation;
        setRequestedOrientation(screenOrientation);
    }
    // endregion

    // region layout variables
    private volatile Integer orientation = null;

    private final ViewLayoutVariables rootVars = new ViewLayoutVariables();

    private final ViewLayoutVariables port_topVars = new ViewLayoutVariables();

    private final ViewLayoutVariables port_sep1Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables port_middleVars = new ViewLayoutVariables();

    private final ViewLayoutVariables port_sep2Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables port_bottomVars = new ViewLayoutVariables();

    private final ViewLayoutVariables prePort_topVars = new ViewLayoutVariables();

    private final ViewLayoutVariables prePort_sep1Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables prePort_middleVars = new ViewLayoutVariables();

    private final ViewLayoutVariables prePort_sep2Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables prePort_bottomVars = new ViewLayoutVariables();

    private final ViewLayoutVariables land_topVars = new ViewLayoutVariables();

    private final ViewLayoutVariables land_sep1Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables land_middleVars = new ViewLayoutVariables();

    private final ViewLayoutVariables land_sep2Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables land_bottomVars = new ViewLayoutVariables();

    private final ViewLayoutVariables preLand_topVars = new ViewLayoutVariables();

    private final ViewLayoutVariables preLand_sep1Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables preLand_middleVars = new ViewLayoutVariables();

    private final ViewLayoutVariables preLand_sep2Vars = new ViewLayoutVariables();

    private final ViewLayoutVariables preLand_bottomVars = new ViewLayoutVariables();

    private void refreshLayoutVariables(int width, int height){
        rootVars.width = width;
        rootVars.height = height;
    }
    // endregion


    // region layout


    private void initLayoutVariables() {
        orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @NonNull
    private volatile Runnable nextLayoutChanged = DelegateUtils.NothingRunnable;

    private void globalLayoutChanged() {
        int height = binding.getRoot().getMeasuredHeight();
        int width = binding.getRoot().getMeasuredWidth();
        if (rootWidth != width || rootHeight != height) {

            rootWidth = width;
            rootHeight = height;

            port_topHeight = (int) (rootWidth * 9f / 16f);
            port_sep1AboveHeight = port_topHeight;
            port_sep1Height = 50;
            port_sep1BelowHeight = port_sep1AboveHeight + port_sep1Height;
            port_middleHeight = (int) (rootWidth * 9f / 16f);
            port_sep2AboveHeight = port_sep1BelowHeight + port_middleHeight;
            port_sep2Height = 50;
            port_sep2BelowHeight = port_sep2AboveHeight + port_sep2Height;
            port_bottomHeight = rootHeight - port_sep2BelowHeight;

            land_topWidth = (int) (rootHeight * 9f / 16f);
            land_sep1AboveWidth = land_topWidth;
            land_sep1Width = 50;
            land_sep1BelowWidth = land_sep1AboveWidth + land_sep1Width;
            land_middleWidth = (int) (rootHeight * 9f / 16f);
            land_sep2AboveWidth = land_sep1BelowWidth + land_middleWidth;
            land_sep2Width = 50;
            land_sep2BelowWidth = land_sep2AboveWidth + land_sep2Width;
            land_bottomWidth = rootWidth - land_sep2BelowWidth;
            LogHelper.d("[width: %d, height: %d, orientation, %d]", rootWidth, rootHeight, orientation);
            this.nextLayoutChanged.run();
        }
    }

    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> viewLayout_port_top = it -> {
        it.set(PROP_WIDTH, CURRENT(), h -> h.rootWidth);
        it.set(PROP_HEIGHT, CURRENT(), h -> h.port_topHeight);
        it.set(PROP_LEFT, CURRENT(), GET_ZERO());
        it.set(PROP_TOP, CURRENT(), GET_ZERO());
    };
    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> viewLayout_port_middle = it -> {
        it.set(PROP_WIDTH, CURRENT(), h -> h.rootWidth);
        it.set(PROP_HEIGHT, CURRENT(), h -> h.port_middleHeight);
        it.set(PROP_LEFT, CURRENT(), GET_ZERO());
        it.set(PROP_TOP, CURRENT(), h -> h.port_sep1BelowHeight);
    };
    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> viewLayout_port_bottom = it -> {
        it.set(PROP_WIDTH, CURRENT(), h -> h.rootWidth);
        it.set(PROP_HEIGHT, CURRENT(), h -> h.port_bottomHeight);
        it.set(PROP_LEFT, CURRENT(), GET_ZERO());
        it.set(PROP_TOP, CURRENT(), h -> h.port_sep2BelowHeight);
    };

    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> viewLayout_land_top = it -> {
        it.set(PROP_WIDTH, h -> h.land_topWidth);
        it.set(PROP_HEIGHT, h -> h.rootHeight);
        it.set(PROP_LEFT, GET_ZERO());
        it.set(PROP_TOP, GET_ZERO());
    };
    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> viewLayout_land_middle = it -> {
        it.set(PROP_WIDTH, h -> h.land_middleWidth);
        it.set(PROP_HEIGHT, h -> h.rootHeight);
        it.set(PROP_LEFT, h -> h.land_sep1BelowWidth);
        it.set(PROP_TOP, GET_ZERO());
    };
    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> viewLayout_land_bottom = it -> {
        it.set(PROP_WIDTH, h -> h.land_bottomWidth);
        it.set(PROP_HEIGHT, h -> h.rootHeight);
        it.set(PROP_LEFT, h -> h.land_sep2BelowWidth);
        it.set(PROP_TOP, GET_ZERO());
    };

    final LayoutTemplate<MainActivity> pre_landscape = new LayoutTemplate<MainActivity>()
            .setDest(LayoutDestination.builder(MainActivity.class)
                    //
                    .beginConfig(h -> h.binding.mainTopContainer)
                    .set(PROP_WIDTH, h -> h.port_topHeight)
                    .set(PROP_HEIGHT, h -> h.rootWidth)
                    .set(PROP_LEFT, GET_ZERO())
                    .set(PROP_TOP, GET_ZERO())
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainMiddleContainer)
                    .set(PROP_WIDTH, h -> h.port_middleHeight)
                    .set(PROP_HEIGHT, h -> h.rootWidth)
                    .set(PROP_LEFT, h -> h.port_sep1BelowHeight)
                    .set(PROP_TOP, GET_ZERO())
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainBottomContainer)
                    .set(PROP_WIDTH, h -> h.port_bottomHeight)
                    .set(PROP_HEIGHT, h -> h.rootWidth)
                    .set(PROP_LEFT, h -> h.port_sep2BelowHeight)
                    .set(PROP_TOP, GET_ZERO())
                    .endConfig()
                    //
                    .build());

    final LayoutTemplate<MainActivity> landscape = new LayoutTemplate<MainActivity>()
            .setPreAction(() -> {
                binding.mainTopContainer.setZ(1f);
                binding.mainMiddleContainer.setZ(0f);
                binding.mainBottomContainer.setZ(0f);
            })
            .setDest(LayoutDestination.builder(MainActivity.class)
                    //
                    .beginConfig(h -> h.binding.mainTopContainer)
                    .apply(VIEW_FULLSCREEN(h -> h.rootWidth, h -> h.rootHeight))
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainMiddleContainer)
                    .apply(VIEW_HIDE_RIGHT(h -> h.rootWidth))
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainBottomContainer)
                    .apply(VIEW_HIDE_RIGHT(h -> h.rootWidth))
                    .endConfig()
                    //
                    .build());
    final LayoutTemplate<MainActivity> pre_portrait = new LayoutTemplate<MainActivity>()
            .setDest(LayoutDestination.builder(MainActivity.class)
                    //
                    .beginConfig(h -> h.binding.mainTopContainer)
                    .apply(VIEW_FULLSCREEN(h -> h.rootHeight, h -> h.rootWidth))
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainMiddleContainer)
                    .set(PROP_WIDTH, h -> h.rootWidth)
                    .set(PROP_HEIGHT, GET_ZERO())
                    .set(PROP_LEFT, GET_ZERO())
                    .set(PROP_TOP, h -> h.rootWidth)
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainBottomContainer)
                    .set(PROP_WIDTH, h -> h.rootWidth)
                    .set(PROP_HEIGHT, GET_ZERO())
                    .set(PROP_LEFT, GET_ZERO())
                    .set(PROP_TOP, h -> h.rootWidth)
                    .endConfig()
                    //
                    .build());
    final LayoutTemplate<MainActivity> portrait = new LayoutTemplate<MainActivity>()
            .setDest(LayoutDestination.builder(MainActivity.class)
                    //
                    .beginConfig(h -> h.binding.mainTopContainer)
                    .apply(viewLayout_port_top)
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainMiddleContainer)
                    .apply(viewLayout_port_middle)
                    .endConfig()
                    //
                    .beginConfig(h -> h.binding.mainBottomContainer)
                    .apply(viewLayout_port_bottom)
                    .endConfig()
                    //
                    .build());
    // endregion

    // region hide UI
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
    // endregion
}
