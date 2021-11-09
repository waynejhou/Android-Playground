package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.FOCUS_TOP;
import static org.waynezhou.androidplayground.main.LayoutChangedReason.*;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libUtil.delegate.DelegateUtils;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libView.transition.ViewAnimatorArgs;
import org.waynezhou.libView.transition.ViewTransition;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

interface ILayout {
    ActivityMainBinding getBinding();
    
    Layouts getLayouts();
    
    boolean isContentViewSet();
    
    void setLandCurrent(ViewTransition<Layouts> landCurrent);
    
    void setPortCurrent(ViewTransition<Layouts> portCurrent);
    
    ViewTransition<Layouts> getLandCurrent();
    
    ViewTransition<Layouts> getPortCurrent();
    
    ViewTransition<Layouts> getCurrent();
    
    boolean isCurrentLayout(String... tags);
    
    void changeLayout(ViewTransition<Layouts> layout);
    
    void animateLayout(
      ViewTransition<Layouts> layout,
      ViewAnimatorArgs args
    );
}


final class Layout extends ActivityComponent<MainActivity> implements ILayout {
    
    private ActivityMainBinding binding;
    
    @Override
    public ActivityMainBinding getBinding() {
        return binding;
    }
    
    private final Layouts layouts = new Layouts();
    
    @Override
    public Layouts getLayouts() {
        return layouts;
    }
    
    
    @Override
    protected void onHostCreate(Bundle bundle) {
        host.getEvents().on(g -> g.resume, $ -> hideSystemUI());
        control.onGotSignal(this::onControlGotSignal);
        final WindowManager.LayoutParams lp = host.getWindow().getAttributes();
        lp.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
        binding = ActivityMainBinding.inflate(host.getLayoutInflater());
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(this::onGlobalLayoutChanged);
        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(this::onPreDraw);
        binding.getRoot().getViewTreeObserver().addOnDrawListener(this::onRootDraw);
        setChangedReason(CONTENT_VIEW_SET);
        setContentViewSet(false);
        host.setContentView(binding.getRoot());
        layouts.init(host, binding);
    }
    
    // region input route
    private void onRootDraw() {
    }
    
    private boolean onPreDraw() {
        return true;
    }
    
    private void onGlobalLayoutChanged() {
        if (changedReason == null) return;
        LogHelper.i(changedReason);
        if (changedReason.equals(CONTENT_VIEW_SET)) {
            isContentViewSet = true;
            onContentViewSet();
        } else if (changedReason.equals(ROTATION_REQUESTED)) {
            onRotationRequested();
            onceRotationRequested.run();
            onceRotationRequested = DelegateUtils.NothingRunnable;
        }
        changedReason = null;
    }
    
    private void onContentViewSet() {
        layouts.refreshLayoutProperties();
        if (rotate.isLand()) {
            layout(landCurrent);
            
        } else if (rotate.isPort()) {
            layout(portCurrent);
        }
    }
    
    private void onRotationRequested() {
        layouts.refreshLayoutProperties();
        //layoutAuto(ViewAnimatorArgs.builder().setDuration(500).build());
    }
    // endregion
    
    
    // region get set property
    @NonNull
    private Runnable onceRotationRequested = DelegateUtils.NothingRunnable;
    
    void setOnceRotationRequested(@NonNull Runnable run) {
        onceRotationRequested = run;
    }
    
    private LayoutChangedReason changedReason = null;
    
    LayoutChangedReason getChangedReason() {
        return changedReason;
    }
    
    void setChangedReason(LayoutChangedReason changedReason) {
        this.changedReason = changedReason;
    }
    
    private boolean isContentViewSet = false;
    
    @Override
    public boolean isContentViewSet() {
        return isContentViewSet;
    }
    
    @SuppressWarnings("SameParameterValue")
    void setContentViewSet(boolean contentViewSet) {
        isContentViewSet = contentViewSet;
    }
    
    private ViewTransition<Layouts> landCurrent = layouts.land_top;
    
    @Override
    public void setLandCurrent(ViewTransition<Layouts> landCurrent) {
        this.landCurrent = landCurrent;
    }
    
    @Override
    public ViewTransition<Layouts> getLandCurrent() {
        return landCurrent;
    }
    
    private ViewTransition<Layouts> portCurrent = layouts.port_std;
    
    @Override
    public void setPortCurrent(ViewTransition<Layouts> portCurrent) {
        this.portCurrent = portCurrent;
    }
    
    @Override
    public ViewTransition<Layouts> getPortCurrent() {
        return portCurrent;
    }
    
    @Override
    public ViewTransition<Layouts> getCurrent() {
        if (rotate.isPort()) {
            return portCurrent;
        }
        return landCurrent;
    }
    
    @Override
    public boolean isCurrentLayout(String... tags) {
        return isLayout(getCurrent(), tags);
    }
    
    private boolean isLayout(
      ViewTransition<Layouts> layout,
      String... tags
    ) {
        List<String> tagList = Arrays.asList(Objects.requireNonNull(layouts.layoutTags.get(layout)));
        for (String tag : tags) {
            if (!tagList.contains(tag)) return false;
        }
        return true;
    }
    // endregion
    
    // region output action
    
    private void onControlGotSignal(ControlSignal signal) {
        if (CTRL_LAYOUT_PORT_STD.equals(signal)) {
            toPort(layouts.port_std);
        } else if (CTRL_LAYOUT_PORT_HALF.equals(signal)) {
            toPortHalf();
        } else if (CTRL_LAYOUT_PORT_FULL.equals(signal)) {
            toPortFull();
        } else if (CTRL_LAYOUT_PORT_TOP_FULL.equals(signal)) {
            toPort(layouts.port_topFull);
        } else if (CTRL_LAYOUT_PORT_TOP_HALF.equals(signal)) {
            toPort(layouts.port_topHalf);
        } else if (CTRL_LAYOUT_PORT_MIDDLE_FULL.equals(signal)) {
            toPort(layouts.port_middleFull);
        } else if (CTRL_LAYOUT_PORT_MIDDLE_HALF.equals(signal)) {
            toPort(layouts.port_middleHalf);
        }
    }
    
    private void toPortHalf() {
        if (rotate.isNotPort()) return;
        if (focusView.getFocusPos() == FOCUS_TOP) {
            toPort(layouts.port_topHalf);
        }
        if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            toPort(layouts.port_middleHalf);
        }
    }
    
    private void toPortFull() {
        if (rotate.isNotPort()) return;
        if (focusView.getFocusPos() == FOCUS_TOP) {
            toPort(layouts.port_topFull);
        }
        if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            toPort(layouts.port_middleFull);
        }
    }
    
    private void toPort(ViewTransition<Layouts> newLayout) {
        if (rotate.isNotPort()) return;
        setPortCurrent(newLayout);
        animateLayout(getPortCurrent());
    }
    
    
    private final static ViewAnimatorArgs DefaultAnimatorArgs = ViewAnimatorArgs.builder()
      .setDuration(500)
      .setInterpolator(new DecelerateInterpolator())
      .build();
    
    private boolean isLayoutAnimating = false;
    private AnimatorSet animatingAnimator = null;
    private final Animator.AnimatorListener animatingFlagSettingListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            isLayoutAnimating = true;
        }
        
        @Override
        public void onAnimationEnd(Animator animation) {
            isLayoutAnimating = false;
            animatingAnimator = null;
        }
        
        @Override
        public void onAnimationCancel(Animator animation) {
            isLayoutAnimating = false;
            animatingAnimator = null;
        }
        
        @Override
        public void onAnimationRepeat(Animator animation) {
        
        }
    };
    
    @Override
    public void changeLayout(ViewTransition<Layouts> transition) {
        transition.runWithoutAnimation(true);
    }
    
    @Override
    public void animateLayout(
      ViewTransition<Layouts> layout,
      ViewAnimatorArgs args
    ) {
        if (animatingAnimator != null) animatingAnimator.cancel();
        animatingAnimator = layout.createAnimatorSet(args);
        animatingAnimator.addListener(animatingFlagSettingListener);
        animatingAnimator.start();
    }
    
    private void animateLayout(ViewTransition<Layouts> layout) {
        animateLayout(layout, DefaultAnimatorArgs);
    }
    
    
    private void hideSystemUI() {
        View decorView = host.getWindow().getDecorView();
        int uiOptions = //View.SYSTEM_UI_FLAG_LOW_PROFILE
          View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = host.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    // endregion
}

