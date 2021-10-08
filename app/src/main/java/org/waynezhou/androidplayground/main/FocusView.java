package org.waynezhou.androidplayground.main;

final class FocusView {
    private Activity host;

    void init(Activity activity) {
        this.host = activity;
    }

    private FocusPosition focusPos = FocusPosition.FOCUS_TOP;

    FocusPosition getFocusPos() {
        return focusPos;
    }

    void focusNext() {
        if (host.rotate.isLand()) return;
        if (host.rotate.isPort() &&
                (host.layout.getPortCurrent() == host.layout.port_topFull
                        ||host.layout.getPortCurrent() == host.layout.port_middleFull))
            return;
        FocusPosition nextPos = (FocusPosition) FocusPosition.getMap().get(Math.min(Math.max(focusPos.identifier + 1, 0), 2));
        if (host.rotate.isPort() && host.layout.getPortCurrent() == host.layout.port_topHalf &&
                nextPos == FocusPosition.FOCUS_MIDDLE
        ) {
            nextPos = FocusPosition.FOCUS_BOTTOM;
        }
        if (host.rotate.isPort() && host.layout.getPortCurrent() == host.layout.port_middleHalf &&
                nextPos == FocusPosition.FOCUS_TOP
        ) {
            nextPos = FocusPosition.FOCUS_MIDDLE;
        }
        focus(nextPos);
    }

    void focusPrev() {
        if (host.rotate.isLand()) return;
        if (host.rotate.isPort() &&
                (host.layout.getPortCurrent() == host.layout.port_topFull
                ||host.layout.getPortCurrent() == host.layout.port_middleFull))
            return;
        FocusPosition nextPos = (FocusPosition) FocusPosition.getMap().get(Math.min(Math.max(focusPos.identifier - 1, 0), 2));
        if (host.rotate.isPort() && host.layout.getPortCurrent() == host.layout.port_topHalf &&
                nextPos == FocusPosition.FOCUS_MIDDLE
        ) {
            nextPos = FocusPosition.FOCUS_TOP;
        }
        if (host.rotate.isPort() && host.layout.getPortCurrent() == host.layout.port_middleHalf &&
                nextPos == FocusPosition.FOCUS_TOP
        ) {
            nextPos = FocusPosition.FOCUS_MIDDLE;
        }
        focus(nextPos);
    }

    void focus(FocusPosition fPos) {
        if (focusPos==fPos) return;
        focusPos = fPos;
        host.layout.layoutAnimated(host.layout.getCurrent());
    }
}
