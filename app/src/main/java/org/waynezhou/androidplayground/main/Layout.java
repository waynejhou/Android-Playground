package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.FOCUS_TOP;
import static org.waynezhou.androidplayground.main.LayoutChangedReason.*;
import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.*;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.standard.StandardKt;
import org.waynezhou.libView.transition.ViewAnimatorArgs;
import org.waynezhou.libView.transition.ViewStep;
import org.waynezhou.libView.transition.ViewTransition;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
final class Layout {
    private Activity host;
    private Layout layout;
    private Rotate rotate;
    private FocusView focusView;
    private Control control;
    
    void init(Activity activity) {
        this.host = activity;
        this.layout = host.layout;
        this.focusView = host.focusView;
        this.rotate = host.rotate;
        this.control = host.control;
        host.getEvents().on(g -> g.create, this::onHostCreate);
        host.getEvents().on(g -> g.resume, $ -> hideSystemUI());
    }
    
    ActivityMainBinding binding;
    
    private void onHostCreate(Bundle bundle) {
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
    }
    
    // region input route
    void onRootDraw() {
    }
    
    boolean onPreDraw() {
        return true;
    }
    
    void onGlobalLayoutChanged() {
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
        refreshLayoutProperties();
        if (rotate.isLand()) {
            layout(landCurrent);
            
        } else if (rotate.isPort()) {
            layout(portCurrent);
        }
    }
    
    private void onRotationRequested() {
        refreshLayoutProperties();
        //layoutAuto(ViewAnimatorArgs.builder().setDuration(500).build());
    }
    // endregion
    
    // region layouts
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
    
    private final ViewStep.ViewPreBuild<Layout> topContainerHide = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_topContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> -h.port_topContainer_height).end();
    
    private final ViewStep.ViewPreBuild<Layout> topContainerHideTranspose = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.port_topContainer_height).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> -h.port_topContainer_height).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layout> middleContainer = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_middleContainer_top).end();
    
    private final ViewStep.ViewPreBuild<Layout> middleContainerHideTranspose = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layout> middleContainerHide = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end();
    
    private final ViewStep.ViewPreBuild<Layout> bottomContainer = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end();
    
    private final ViewStep.ViewPreBuild<Layout> bottomContainerHideTranspose = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layout> bottomContainerHide = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end();
    
    private final ViewStep.ViewPreBuild<Layout> landFull = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layout> portFull = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layout> portTopHalf = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    final ViewTransition<Layout> land_top = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(landFull)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(middleContainerHideTranspose)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainerHideTranspose)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(landFull)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layout> land_middle = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(topContainerHideTranspose)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(landFull)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainerHideTranspose)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(landFull)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layout> port_std = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(topContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() != FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(middleContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() != FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(topContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(middleContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(bottomContainer)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layout> port_topFull = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(portFull)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(middleContainerHide)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainerHide)
          .$_endAddStep();
        
        /* mainFocusView */
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(portFull)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(middleContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(bottomContainerHide)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layout> port_topHalf = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(portTopHalf)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() != FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(middleContainerHide)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end()
          .$_endAddStep();
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
        /* mainFocusView */
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(portTopHalf)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(middleContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layout> port_middleFull = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f))
          .preBuild(topContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f))
          .preBuild(portFull)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f))
          .preBuild(bottomContainerHide)
          .$_endAddStep();
        /* mainFocusView */
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(topContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(portFull)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(bottomContainerHide)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layout> port_middleHalf = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f))
          .preBuild(topContainerHide)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(portTopHalf)
          .$_endAddStep();
        
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() != FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
        /* mainFocusView */
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(topContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(portTopHalf)
          .$_endAddStep();
        it.$_startAddStep(() -> focusView.getFocusPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
    })
      .build();
    
    
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
    
    boolean isContentViewSet() {
        return isContentViewSet;
    }
    
    @SuppressWarnings("SameParameterValue")
    void setContentViewSet(boolean contentViewSet) {
        isContentViewSet = contentViewSet;
    }
    
    private ViewTransition<Layout> landCurrent = land_top;
    
    void setLandCurrent(ViewTransition<Layout> landCurrent) {
        this.landCurrent = landCurrent;
    }
    
    ViewTransition<Layout> getLandCurrent() {
        return landCurrent;
    }
    
    private ViewTransition<Layout> portCurrent = port_std;
    
    void setPortCurrent(ViewTransition<Layout> portCurrent) {
        this.portCurrent = portCurrent;
    }
    
    ViewTransition<Layout> getPortCurrent() {
        return portCurrent;
    }
    
    ViewTransition<Layout> getCurrent() {
        if (rotate.isPort()) {
            return portCurrent;
        }
        return landCurrent;
    }
    
    boolean isCurrentLand() {
        return isLayoutLand(getCurrent());
    }
    
    boolean isLayoutLand(ViewTransition<Layout> layout) {
        return layout == land_top
          || layout == land_middle
          ;
    }
    
    boolean isCurrentPort() {
        return isLayoutPort(getCurrent());
    }
    
    boolean isLayoutPort(ViewTransition<Layout> layout) {
        return layout == port_std
          || layout == port_topHalf
          || layout == port_topFull
          || layout == port_middleHalf
          || layout == port_middleFull
          ;
    }
    
    boolean isCurrentPortHalf() {
        return isLayoutPortHalf(getCurrent());
    }
    
    boolean isLayoutPortHalf(ViewTransition<Layout> layout) {
        return layout == port_middleHalf
          || layout == port_topHalf;
    }
    
    boolean isCurrentPortFull() {
        return isLayoutPortFull(getCurrent());
    }
    
    boolean isLayoutPortFull(ViewTransition<Layout> layout) {
        return layout == port_middleFull
          || layout == port_topFull;
    }
    
    boolean isCurrentPortStd() {
        return isLayoutPortStd(getCurrent());
    }
    
    boolean isLayoutPortStd(ViewTransition<Layout> layout) {
        return layout == port_std;
    }
    // endregion
    
    // region output action
    
    private void onControlGotSignal(ControlSignal signal) {
        if (CTRL_LAYOUT_PORT_STD.equals(signal)) {
            toPort(port_std);
        } else if (CTRL_LAYOUT_PORT_HALF.equals(signal)) {
            toPortHalf();
        } else if (CTRL_LAYOUT_PORT_FULL.equals(signal)) {
            toPortFull();
        } else if (CTRL_LAYOUT_PORT_TOP_FULL.equals(signal)) {
            toPort(port_topFull);
        } else if (CTRL_LAYOUT_PORT_TOP_HALF.equals(signal)) {
            toPort(port_topHalf);
        } else if (CTRL_LAYOUT_PORT_MIDDLE_FULL.equals(signal)) {
            toPort(port_middleFull);
        } else if (CTRL_LAYOUT_PORT_MIDDLE_HALF.equals(signal)) {
            toPort(port_middleHalf);
        }
    }
    
    private void toPortHalf() {
        if (rotate.isNotPort()) return;
        if (focusView.getFocusPos() == FOCUS_TOP) {
            toPort(port_topHalf);
        }
        if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            toPort(port_middleHalf);
        }
    }
    
    private void toPortFull() {
        if (rotate.isNotPort()) return;
        if (focusView.getFocusPos() == FOCUS_TOP) {
            toPort(port_topFull);
        }
        if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            toPort(port_middleFull);
        }
    }
    private void toPort(ViewTransition<Layout> newLayout) {
        if (rotate.isNotPort()) return;
        layout.setPortCurrent(newLayout);
        layout.layoutAnimated(layout.getPortCurrent());
    }
    
    
    void layout(ViewTransition<Layout> transition) {
        transition.runWithoutAnimation(true);
    }
    
    private final static ViewAnimatorArgs DefaultAnimatorArgs = ViewAnimatorArgs.builder()
      .setDuration(500)
      .setInterpolator(new DecelerateInterpolator())
      .build();
    
    void layoutAnimated(ViewTransition<Layout> transition) {
        layoutAnimated(transition, DefaultAnimatorArgs);
    }
    
    private boolean isLayoutAnimating = false;
    private AnimatorSet animatingAnimator = null;
    
    @SuppressWarnings("SameParameterValue")
    void layoutAnimated(ViewTransition<Layout> transition, ViewAnimatorArgs args) {
        if (animatingAnimator != null) animatingAnimator.cancel();
        animatingAnimator = transition.createAnimatorSet(args);
        animatingAnimator.addListener(new Animator.AnimatorListener() {
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
        });
        animatingAnimator.start();
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
