package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.*;

import org.waynezhou.libUtil.LogHelper;

final class Control {
    private Activity host;

    void init(Activity activity) {
        this.host = activity;
    }

    void sendSignal(ControlSignal signal) {
        onGotSignal(signal);
    }

    private void onGotSignal(ControlSignal signal) {
        LogHelper.i(signal);
        if (CTRL_ROTATE.equals(signal)) {
            host.rotate.auto();
        } else if (CTRL_ROTATE_LAND.equals(signal)) {
            host.rotate.toLand();
        } else if (CTRL_ROTATE_PORT.equals(signal)) {
            host.rotate.toPort();
        } else if (CTRL_LAYOUT_PORT_STD.equals(signal)) {
            if (host.rotate.isPort()) {
                host.layout.setPortCurrent(host.layout.port_std);
                host.layout.layoutAnimated(host.layout.getPortCurrent());
            }
        } else if (CTRL_LAYOUT_PORT_TOP_HALF.equals(signal)) {
            if (host.rotate.isPort()) {
                if (host.focusView.getFocusPos() == FOCUS_TOP) {
                    host.layout.setPortCurrent(host.layout.port_topHalf);
                    host.layout.layoutAnimated(host.layout.getPortCurrent());
                }
                if (host.focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
                    host.layout.setPortCurrent(host.layout.port_middleHalf);
                    host.layout.layoutAnimated(host.layout.getPortCurrent());
                }

            }
        } else if (CTRL_LAYOUT_PORT_TOP_FULL.equals(signal)) {
            if (host.rotate.isPort()) {
                if (host.focusView.getFocusPos() == FOCUS_TOP) {
                    host.layout.setPortCurrent(host.layout.port_topFull);
                    host.layout.layoutAnimated(host.layout.getPortCurrent());
                }
                if (host.focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
                    host.layout.setPortCurrent(host.layout.port_middleFull);
                    host.layout.layoutAnimated(host.layout.getPortCurrent());
                }
            }
        } else if (CTRL_MEDIA_NEXT_SECTION.equals(signal)) {
            if (host.focusView.getFocusPos() == FOCUS_TOP) {
                host.mediaTop.toNextSection();
            } else if (host.focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
                host.mediaMiddle.toNextSection();
            }
        } else if (CTRL_MEDIA_PREV_SECTION.equals(signal)) {
            if (host.focusView.getFocusPos() == FOCUS_TOP) {
                host.mediaTop.toPrevSection();
            } else if (host.focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
                host.mediaMiddle.toPrevSection();
            }
        } else if (CTRL_FOCUS_NEXT.equals(signal)) {
            host.focusView.focusNext();
        } else if (CTRL_FOCUS_PREV.equals(signal)) {
            host.focusView.focusPrev();
        }else if (CTRL_FOCUS_TOP.equals(signal)) {
            host.focusView.focus(FOCUS_TOP);
        } else if (CTRL_FOCUS_MIDDLE.equals(signal)) {
            host.focusView.focus(FOCUS_MIDDLE);
        }
        else if (CTRL_FOCUS_BOTTOM.equals(signal)) {
            host.focusView.focus(FOCUS_BOTTOM);
        }
    }
}