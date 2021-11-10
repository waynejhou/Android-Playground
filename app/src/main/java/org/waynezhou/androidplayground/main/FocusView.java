package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.*;

import android.os.Bundle;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.activity.ActivityComponent;

interface IFocusView {
    void focusViewOnNext();
    void focusViewOnPrev();
    void focusView(@NonNull FocusPosition fPos);
    @NonNull FocusPosition getFocusViewPos();
}

final class FocusView extends ActivityComponent<MainActivity> implements IFocusView {
    
    
    @Override
    public void onHostCreate(Bundle bundle) {
        host.onControlGotSignal(this::onControlGotSignal);
    }
    
    private void onControlGotSignal(ControlSignal signal) {
        if (CTRL_FOCUS_NEXT.equals(signal)) {
            focusViewOnNext();
        } else if (CTRL_FOCUS_PREV.equals(signal)) {
            focusViewOnPrev();
        } else if (CTRL_FOCUS_TOP.equals(signal)) {
            focusView(FOCUS_TOP);
        } else if (CTRL_FOCUS_MIDDLE.equals(signal)) {
            focusView(FOCUS_MIDDLE);
        } else if (CTRL_FOCUS_BOTTOM.equals(signal)) {
            focusView(FOCUS_BOTTOM);
        }
    }
    
    private FocusPosition focusPos = FocusPosition.FOCUS_TOP;
    
    @NonNull
    @Override
    public FocusPosition getFocusViewPos() {
        return focusPos;
    }
    
    @Override
    public void focusViewOnNext() {
        if (host.isCurrentLayout(Layouts.tag_land)) return;
        if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_full))
            return;
        FocusPosition nextPos = FocusPosition.getMap(FocusPosition.class).get(Math.min(Math.max(focusPos.identifier + 1, 0), 2));
        if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_half) && nextPos == FocusPosition.FOCUS_MIDDLE
        ) {
            nextPos = FocusPosition.FOCUS_BOTTOM;
        }
        if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_half) && nextPos == FocusPosition.FOCUS_TOP
        ) {
            nextPos = FocusPosition.FOCUS_MIDDLE;
        }
        focusView(nextPos);
    }
    
    @Override
    public void focusViewOnPrev() {
        if (host.isCurrentLayout(Layouts.tag_land)) return;
        if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_full))
            return;
        FocusPosition nextPos = FocusPosition.getMap(FocusPosition.class).get(Math.min(Math.max(focusPos.identifier - 1, 0), 2));
        if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_half) && nextPos == FocusPosition.FOCUS_MIDDLE
        ) {
            nextPos = FocusPosition.FOCUS_TOP;
        }
        if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_half) && nextPos == FocusPosition.FOCUS_TOP
        ) {
            nextPos = FocusPosition.FOCUS_MIDDLE;
        }
        focusView(nextPos);
    }
    
    @Override
    public void focusView(@NonNull FocusPosition fPos) {
        if (focusPos == fPos) return;
        focusPos = fPos;
        if (focusPos == FocusPosition.FOCUS_TOP) {
            host.setLandCurrent(host.getLayouts().land_top);
        }
        if (focusPos == FocusPosition.FOCUS_MIDDLE) {
            host.setLandCurrent(host.getLayouts().land_middle);
        }
        host.animateLayout(host.getCurrent(), Layout.DefaultAnimatorArgs);
    }
}
