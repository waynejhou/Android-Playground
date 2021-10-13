package org.waynezhou.androidplayground.main;

final class FocusView {
    private Activity host;
    private Rotate rotate;
    private Layout layout;
    void init(Activity activity) {
        this.host = activity;
        rotate = host.rotate;
        layout = host.layout;
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
