package org.waynezhou.androidplayground.main;

import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.PROP_HEI;
import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.PROP_LFT;
import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.PROP_SCX;
import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.PROP_SCY;
import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.PROP_TOP;
import static org.waynezhou.libView.transition.LayoutTransitionPropertyBridges.PROP_WID;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libUtil.standard.StandardKt;
import org.waynezhou.libView.transition.ViewStep;
import org.waynezhou.libView.transition.ViewTransition;

import java.util.HashMap;
import java.util.Map;

class Layouts {
    private MainActivity host;
    private ActivityMainBinding binding;
    void init(MainActivity host, ActivityMainBinding binding){
        this.host = host;
        this.binding = binding;
    }
    
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
    
    void refreshLayoutProperties() {
        final int width = binding.getRoot().getWidth();
        final int height = binding.getRoot().getHeight();
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
    
    private final ViewStep.ViewPreBuild<Layouts> topContainer = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_topContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_topContainer_top).end();
    
    private final ViewStep.ViewPreBuild<Layouts> topContainerHide = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_topContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> -h.port_topContainer_height).end();
    
    private final ViewStep.ViewPreBuild<Layouts> topContainerHideTranspose = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.port_topContainer_height).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> -h.port_topContainer_height).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layouts> middleContainer = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_middleContainer_top).end();
    
    private final ViewStep.ViewPreBuild<Layouts> middleContainerHideTranspose = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layouts> middleContainerHide = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_middleContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end();
    
    private final ViewStep.ViewPreBuild<Layouts> bottomContainer = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end();
    
    private final ViewStep.ViewPreBuild<Layouts> bottomContainerHideTranspose = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layouts> bottomContainerHide = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_height).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> h.longSideLength).end();
    
    private final ViewStep.ViewPreBuild<Layouts> landFull = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layouts> portFull = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.longSideLength).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    private final ViewStep.ViewPreBuild<Layouts> portTopHalf = it -> it
      .let(PROP_WID).startFromCurrent().toStep(h -> h.shortSideLength).end()
      .let(PROP_HEI).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
      .let(PROP_LFT).startFromCurrent().toStep(h -> 0).end()
      .let(PROP_TOP).startFromCurrent().toStep(h -> 0).end();
    
    final ViewTransition<Layouts> land_top = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
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
    
    final ViewTransition<Layouts> land_middle = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
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
    
    final ViewTransition<Layouts> port_std = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(topContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() != FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(middleContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() != FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(topContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(middleContainer)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(bottomContainer)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layouts> port_topFull = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
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
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(portFull)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(middleContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(bottomContainerHide)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layouts> port_topHalf = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f)).preBuild(portTopHalf)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() != FocusPosition.FOCUS_TOP, () -> binding.mainTopContainer)
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
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(portTopHalf)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(middleContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layouts> port_middleFull = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
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
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(topContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(portFull)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(bottomContainerHide)
          .$_endAddStep();
    })
      .build();
    
    final ViewTransition<Layouts> port_middleHalf = StandardKt.apply(new ViewTransition.Builder<>(this), it -> {
        it.$_startAddStep(() -> binding.mainTopContainer)
          .pre(v -> v.setZ(1f))
          .preBuild(topContainerHide)
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainMiddleContainer)
          .pre(v -> v.setZ(0.5f)).preBuild(portTopHalf)
          .$_endAddStep();
        
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 0.90f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 0.90f).end()
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() != FocusPosition.FOCUS_MIDDLE, () -> binding.mainMiddleContainer)
          .let(PROP_SCX).startFromCurrent().toStep(h -> 1f).end()
          .let(PROP_SCY).startFromCurrent().toStep(h -> 1f).end()
          .$_endAddStep();
        
        it.$_startAddStep(() -> binding.mainBottomContainer)
          .pre(v -> v.setZ(0f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
        /* mainFocusView */
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_TOP, () -> binding.mainFocusView)
          .pre(v -> v.setZ(0)).preBuild(topContainerHide)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_MIDDLE, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1)).preBuild(portTopHalf)
          .$_endAddStep();
        it.$_startAddStep(() -> host.getFocusViewPos() == FocusPosition.FOCUS_BOTTOM, () -> binding.mainFocusView)
          .pre(v -> v.setZ(1f)).preBuild(bottomContainer)
          .let(PROP_TOP).startFromCurrent().toStep(h -> h.port_bottomContainer_top).end()
          .$_endAddStep();
    })
      .build();
    
    static final String tag_land = "land";
    static final String tag_port = "port";
    static final String tag_top = "top";
    static final String tag_middle = "middle";
    static final String tag_std = "std";
    static final String tag_full = "full";
    static final String tag_half = "half";
    Map<ViewTransition<Layouts>, String[]> layoutTags = new HashMap<ViewTransition<Layouts>, String[]>(){{
        put(land_top, new String[]{tag_land, tag_top});
        put(land_middle, new String[]{tag_land, tag_middle});
        put(port_std, new String[]{tag_port, tag_std});
        put(port_topFull, new String[]{tag_top, tag_full});
        put(port_topHalf, new String[]{tag_middle, tag_half});
        put(port_middleFull, new String[]{tag_middle, tag_full});
        put(port_middleHalf, new String[]{tag_middle, tag_half});
    }};
    
    // endregion
}


