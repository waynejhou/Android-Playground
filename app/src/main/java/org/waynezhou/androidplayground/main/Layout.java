package org.waynezhou.androidplayground;

import static org.waynezhou.androidplayground.MainActivity.LayoutChangedReason.CONTENT_VIEW_SET;
import static org.waynezhou.libView.view_transition.LayoutTransitionPropertyBridges.PROP_HEI;
import static org.waynezhou.libView.view_transition.LayoutTransitionPropertyBridges.PROP_LFT;
import static org.waynezhou.libView.view_transition.LayoutTransitionPropertyBridges.PROP_TOP;
import static org.waynezhou.libView.view_transition.LayoutTransitionPropertyBridges.PROP_WID;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libView.view_transition.ViewAnimatorArgs;
import org.waynezhou.libView.view_transition.ViewStep;
import org.waynezhou.libView.view_transition.ViewTransition;

@SuppressWarnings("DanglingJavadoc")
public class MainActivityLayout {
    private MainActivity host;

    public MainActivityLayout(MainActivity host) {
        this.host = host;
    }

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

    private MainActivity.LayoutChangedReason changedReason = null;

    private boolean isContentViewSet = false;

    private void onGlobalLayoutChanged() {
        if (changedReason == null) return;
        LogHelper.i(changedReason);
        if (changedReason.equals(CONTENT_VIEW_SET)) {
            isContentViewSet = true;
            onContentViewSet();
        } else if (changedReason.equals(MainActivity.LayoutChangedReason.ROTATION_REQUESTED)) {
            onRotationRequested();
            onceRotationRequested.run();
            onceRotationRequested = DelegateUtils.NothingRunnable;
        }
        changedReason = null;
    }

    private void onContentViewSet() {
        refreshLayoutProperties();
        if (host.rotate.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            layout(landCurrent);

        } else if (host.rotate.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
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
        final int width = host.binding.getRoot().getWidth();
        final int height = host.binding.getRoot().getHeight();
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

    private final ViewTransition<Layout> land_std = new ViewTransition.Builder<>(this)
            .$_startAddStep(() -> host.binding.mainTopContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(landFull)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainMiddleContainer)
            .pre(v -> v.setZ(0.5f))
            .preBuild(middleContainerHideTranspose)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainBottomContainer)
            .pre(v -> v.setZ(0f))
            .preBuild(bottomContainerHideTranspose)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .build();

    private
    final ViewTransition<Layout> port_std = new ViewTransition.Builder<>(this)
            /** main Top Container */
            .$_startAddStep(() -> host.binding.mainTopContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(topContainer)
            .$_endAddStep()
            /** main Middle Container */
            .$_startAddStep(() -> host.binding.mainMiddleContainer)
            .pre(v -> v.setZ(0.5f))
            .preBuild(middleContainer)
            .$_endAddStep()
            /** main Bottom Container */
            .$_startAddStep(() -> host.binding.mainBottomContainer)
            .pre(v -> v.setZ(0f))
            .preBuild(bottomContainer)
            .$_endAddStep()
            /** main Focus View */
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_TOP, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1))
            .preBuild(topContainer)
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_MIDDLE, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1))
            .preBuild(middleContainer)
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_BOTTOM, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1))
            .preBuild(bottomContainer)
            .$_endAddStep()
            .build();

    private final ViewTransition<Layout> port_topFull = new ViewTransition.Builder<>(this)
            .$_startAddStep(() -> host.binding.mainTopContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(portFull)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainMiddleContainer)
            .pre(v -> v.setZ(0.5f))
            .preBuild(middleContainerHide)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainBottomContainer)
            .pre(v -> v.setZ(0f))
            .preBuild(bottomContainerHide)
            .$_endAddStep()
            /* mainFocusView */
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_TOP, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1)).preBuild(portFull)
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_MIDDLE, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_BOTTOM, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .build();

    private final ViewTransition<Layout> port_topHalf = new ViewTransition.Builder<>(this)
            .$_startAddStep(() -> host.binding.mainTopContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(portTopHalf)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainMiddleContainer)
            .pre(v -> v.setZ(0f))
            .preBuild(middleContainerHide)
            .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end()
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainBottomContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(bottomContainer)
            .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
            .$_endAddStep()
            /* mainFocusView */
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_TOP, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1))
            .preBuild(portTopHalf)
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_MIDDLE, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_BOTTOM, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1f))
            .preBuild(bottomContainer)
            .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
            .$_endAddStep()
            .build();

    private final ViewTransition<Layout> port_middleFull = new ViewTransition.Builder<>(this)
            .$_startAddStep(() -> host.binding.mainTopContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(topContainerHide)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainMiddleContainer)
            .pre(v -> v.setZ(0.5f))
            .preBuild(portFull)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainBottomContainer)
            .pre(v -> v.setZ(0f))
            .preBuild(bottomContainerHide)
            .$_endAddStep()
            /* mainFocusView */
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_TOP, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1)).preBuild(portFull)
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_MIDDLE, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_BOTTOM, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .build();

    private final ViewTransition<Layout> port_middleHalf = new ViewTransition.Builder<>(this)
            .$_startAddStep(() -> host.binding.mainTopContainer)
            .pre(v -> v.setZ(1f))
            .preBuild(topContainerHide)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainMiddleContainer)
            .pre(v -> v.setZ(0f)).preBuild(portTopHalf)
            .$_endAddStep()
            .$_startAddStep(() -> host.binding.mainBottomContainer)
            .pre(v -> v.setZ(1f)).preBuild(bottomContainer)
            .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
            .$_endAddStep()
            /* mainFocusView */
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_TOP, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(0))
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_MIDDLE, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1)).preBuild(portTopHalf)
            .$_endAddStep()
            .$_startAddStep(() -> host.focusView.focusPos == MainActivity.FocusPositions.FOCUS_BOTTOM, () -> host.binding.mainFocusView)
            .pre(v -> v.setZ(1f)).preBuild(bottomContainer)
            .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
            .$_endAddStep()
            .build();

    private ViewTransition<Layout> landCurrent = land_std;
    private ViewTransition<Layout> portCurrent = port_std;

    private ViewTransition<Layout> getCurrent() {
        if (host.rotate.isPort()) {
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

    private boolean isLayoutAnimating = false;
    private AnimatorSet animatingAnimator = null;

    private void layoutAnimated(ViewTransition<Layout> transition, ViewAnimatorArgs args) {
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
}
