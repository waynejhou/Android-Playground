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
        host.runOnUiThread(() -> onGotSignal(signal));
        
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
            onGotLayoutPortStd();
        } else if (CTRL_LAYOUT_PORT_HALF.equals(signal)) {
            onGotLayoutPortHalf();
        } else if (CTRL_LAYOUT_PORT_FULL.equals(signal)) {
            onGotLayoutPortFull();
        } else if (CTRL_LAYOUT_PORT_TOP_FULL.equals(signal)) {
            onGotLayoutPortTopFull();
        } else if (CTRL_LAYOUT_PORT_TOP_HALF.equals(signal)) {
            onGotLayoutPortTopHalf();
        } else if (CTRL_LAYOUT_PORT_MIDDLE_FULL.equals(signal)) {
            onGotLayoutPortMiddleFull();
        } else if (CTRL_LAYOUT_PORT_MIDDLE_HALF.equals(signal)) {
            onGotLayoutPortMiddleHalf();
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
        } else if (CTRL_FOCUS_TOP.equals(signal)) {
            host.focusView.focus(FOCUS_TOP);
        } else if (CTRL_FOCUS_MIDDLE.equals(signal)) {
            host.focusView.focus(FOCUS_MIDDLE);
        } else if (CTRL_FOCUS_BOTTOM.equals(signal)) {
            host.focusView.focus(FOCUS_BOTTOM);
        }
    }
    
    private void onGotLayoutPortStd() {
        if (host.rotate.isPort()) {
            host.layout.setPortCurrent(host.layout.port_std);
            host.layout.layoutAnimated(host.layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortFull() {
        if (host.focusView.getFocusPos() == FOCUS_TOP) {
            onGotLayoutPortTopFull();
        }
        if (host.focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            onGotLayoutPortMiddleFull();
        }
    }
    
    private void onGotLayoutPortHalf() {
        if (host.focusView.getFocusPos() == FOCUS_TOP) {
            onGotLayoutPortTopHalf();
        }
        if (host.focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            onGotLayoutPortMiddleHalf();
        }
    }
    
    private void onGotLayoutPortTopFull() {
        if (host.rotate.isPort()) {
            host.layout.setPortCurrent(host.layout.port_topFull);
            host.layout.layoutAnimated(host.layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortTopHalf() {
        if (host.rotate.isPort()) {
            host.layout.setPortCurrent(host.layout.port_topHalf);
            host.layout.layoutAnimated(host.layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortMiddleFull() {
        if (host.rotate.isPort()) {
            host.layout.setPortCurrent(host.layout.port_middleFull);
            host.layout.layoutAnimated(host.layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortMiddleHalf() {
        if (host.rotate.isPort()) {
            host.layout.setPortCurrent(host.layout.port_middleHalf);
            host.layout.layoutAnimated(host.layout.getPortCurrent());
        }
    }
}