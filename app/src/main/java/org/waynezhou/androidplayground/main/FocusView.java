package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.*;
import android.os.Bundle;

final class FocusView {
    private MainActivity host;
    private Rotate rotate;
    private Layout layout;
    private Control control;
    void init(MainActivity activity) {
        this.host = activity;
        rotate = host.rotate;
        layout = host.layout;
        control = host.control;
        host.getEvents().on(e->e.create, this::onHostCreate);
    }
    
    private void onHostCreate(Bundle bundle) {
        control.onGotSignal(this::onControlGotSignal);
    }
    
    private void onControlGotSignal(ControlSignal signal) {
        if (CTRL_FOCUS_NEXT.equals(signal)) {
            focusNext();
        } else if (CTRL_FOCUS_PREV.equals(signal)) {
            focusPrev();
        } else if (CTRL_FOCUS_TOP.equals(signal)) {
            focus(FOCUS_TOP);
        } else if (CTRL_FOCUS_MIDDLE.equals(signal)) {
            focus(FOCUS_MIDDLE);
        } else if (CTRL_FOCUS_BOTTOM.equals(signal)) {
            focus(FOCUS_BOTTOM);
        }
    }
    
    private FocusPosition focusPos = FocusPosition.FOCUS_TOP;

    FocusPosition getFocusPos() {
        return focusPos;
    }

    void focusNext() {
        if (rotate.isLand()) return;
        if (rotate.isPort() &&
                (layout.getPortCurrent() == layout.port_topFull
                        ||layout.getPortCurrent() == layout.port_middleFull))
            return;
        FocusPosition nextPos = (FocusPosition) FocusPosition.getMap().get(Math.min(Math.max(focusPos.identifier + 1, 0), 2));
        if (rotate.isPort() && layout.getPortCurrent() == layout.port_topHalf &&
                nextPos == FocusPosition.FOCUS_MIDDLE
        ) {
            nextPos = FocusPosition.FOCUS_BOTTOM;
        }
        if (rotate.isPort() && layout.getPortCurrent() == layout.port_middleHalf &&
                nextPos == FocusPosition.FOCUS_TOP
        ) {
            nextPos = FocusPosition.FOCUS_MIDDLE;
        }
        focus(nextPos);
    }

    void focusPrev() {
        if (rotate.isLand()) return;
        if (rotate.isPort() &&
                (layout.getPortCurrent() == layout.port_topFull
                ||layout.getPortCurrent() == layout.port_middleFull))
            return;
        FocusPosition nextPos = (FocusPosition) FocusPosition.getMap().get(Math.min(Math.max(focusPos.identifier - 1, 0), 2));
        if (rotate.isPort() && layout.getPortCurrent() == layout.port_topHalf &&
                nextPos == FocusPosition.FOCUS_MIDDLE
        ) {
            nextPos = FocusPosition.FOCUS_TOP;
        }
        if (rotate.isPort() && layout.getPortCurrent() == layout.port_middleHalf &&
                nextPos == FocusPosition.FOCUS_TOP
        ) {
            nextPos = FocusPosition.FOCUS_MIDDLE;
        }
        focus(nextPos);
    }

    void focus(FocusPosition fPos) {
        if (focusPos==fPos) return;
        focusPos = fPos;
        if(focusPos == FocusPosition.FOCUS_TOP){
            layout.setLandCurrent(layout.land_top);
        }
        if(focusPos == FocusPosition.FOCUS_MIDDLE){
            layout.setLandCurrent(layout.land_middle);
        }
        layout.layoutAnimated(layout.getCurrent());
    }
}
