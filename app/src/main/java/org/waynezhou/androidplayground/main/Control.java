package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.*;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;

final class Control {
    private Activity host;
    private Layout layout;
    private FocusView focusView;
    private Rotate rotate;
    private Control control;
    //private SensorToggle gSensor;
    
    void init(Activity activity) {
        this.host = activity;
        layout = host.layout;
        focusView = host.focusView;
        rotate = host.rotate;
        control = host.control;
        //this.gSensor = new SensorToggle(host, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        //gSensor.getEventGroup().on(e -> e.changed, this::onGSensorValueChanged);
        //this.host.getEventGroup().on(g->g.pause, $->gSensor.Off());
        //this.host.getEventGroup().on(g->g.resume, $->gSensor.On());
        this.host.getEventGroup()
          .on(g -> g.create, this::onHostCreate)
          .on(g -> g.configurationChanged, this::onHostConfigurationChanged)
        .on(g->g.keyDown, this::onHostKeyDown)
        ;
    }
    
    private GestureDetector gestureDetector;
    
    @SuppressLint("ClickableViewAccessibility")
    private void onHostCreate(Bundle bundle) {
        layout.binding.mainTopContainer.setOnClickListener(e -> sendSignal(CTRL_FOCUS_TOP));
        layout.binding.mainMiddleContainer.setOnClickListener(e -> sendSignal(CTRL_FOCUS_MIDDLE));
        layout.binding.mainBottomContainer.setOnClickListener(e -> sendSignal(CTRL_FOCUS_BOTTOM));
        gestureDetector = new GestureDetector(host, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
            
            @Override
            public void onShowPress(MotionEvent e) {
            }
            
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }
            
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }
            
            @Override
            public void onLongPress(MotionEvent e) {
            }
            
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                LogHelper.i("start from x: %f, y: %f, vx: %f, vy: %f", e1.getRawX(), e1.getRawY(), velocityX, velocityY);
                final int x = Math.round(e1.getRawX());
                final int y = Math.round(e1.getRawY());
                Rect focusBBox = new Rect();
                if (layout.binding.mainFocusView.getGlobalVisibleRect(focusBBox)) {
                    LogHelper.i("In focus view: %b %b", focusBBox.contains(x, y), layout.isCurrentPortStd());
                    if (focusBBox.contains(x, y)) {
                        if (Math.abs(velocityX) > Math.abs(velocityY)) {
                            if (velocityX > 1000) {
                                control.sendSignal(CTRL_MEDIA_PREV_SECTION);
                            }
                            if (velocityX < -1000) {
                                control.sendSignal(CTRL_MEDIA_NEXT_SECTION);
                            }
                        } else {
                            if (velocityY > 2000) {
                                if (layout.isCurrentPortStd())
                                    control.sendSignal(CTRL_LAYOUT_PORT_HALF);
                                else if (layout.isCurrentPortHalf())
                                    control.sendSignal(CTRL_LAYOUT_PORT_FULL);
                            }
                            if (velocityY < -2000) {
                                control.sendSignal(CTRL_LAYOUT_PORT_STD);
                            }
                        }
                    }
                }
                return true;
            }
        });
        
        layout.binding.mainTouchArea.setOnTouchListener((v, e) -> {
            //LogHelper.i(MotionEventExplain.explainAction(e.getAction()));
            if (!gestureDetector.onTouchEvent(e)) {
                host.runOnUiThread(() -> layout.binding.mainViewArea.dispatchTouchEvent(e));
            }
            return true;
        });
    }
    
    private void onGSensorValueChanged(SensorChangedEventArgs e) {
        if (!layout.isContentViewSet()) return;
        float[] axis = e.event.values;
        final float x = axis[0];
        final float y = axis[1];
        //final float z = axis[2];
        final float g = SensorManager.STANDARD_GRAVITY;
        //LogHelper.d("x: "+x+" y: "+y);
        if ((Math.abs(x - 0) < 1f && Math.abs(y - g) < 1f) && rotate.isNotPort()) {
            control.sendSignal(CTRL_ROTATE_PORT);
        } else if ((Math.abs(x - g) < 1f && Math.abs(y - 0) < 1f) && rotate.isNotLand()) {
            control.sendSignal(CTRL_ROTATE_LAND);
        }
    }
    
    private void onHostConfigurationChanged(Configuration newConfig) {
        LogHelper.d("new orientation %d", newConfig.orientation);
        if (rotate.isOrientationChanged(newConfig.orientation)) {
            control.sendSignal(CTRL_ROTATE);
        }
    }
    
    private void onHostKeyDown(Activity.KeyDownEventArgs  e) {
        LogHelper.d(e.keyCode);
        if (e.keyCode == KeyEvent.KEYCODE_1) {
            control.sendSignal(CTRL_LAYOUT_PORT_STD);
        }
        if (e.keyCode == KeyEvent.KEYCODE_2) {
            control.sendSignal(CTRL_LAYOUT_PORT_HALF);
        }
        if (e.keyCode == KeyEvent.KEYCODE_3) {
            control.sendSignal(CTRL_LAYOUT_PORT_FULL);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            control.sendSignal(CTRL_MEDIA_PREV_SECTION);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            control.sendSignal(CTRL_MEDIA_NEXT_SECTION);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            control.sendSignal(CTRL_FOCUS_PREV);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            control.sendSignal(CTRL_FOCUS_NEXT);
        }
    }
    
    void sendSignal(ControlSignal signal) {
        host.runOnUiThread(() -> onGotSignal(signal));
        
    }
    
    private void onGotSignal(ControlSignal signal) {
        LogHelper.i(signal);
        if (CTRL_ROTATE.equals(signal)) {
            rotate.auto();
        } else if (CTRL_ROTATE_LAND.equals(signal)) {
            rotate.toLand();
        } else if (CTRL_ROTATE_PORT.equals(signal)) {
            rotate.toPort();
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
        } /*else if (CTRL_MEDIA_NEXT_SECTION.equals(signal)) {
            if (focusView.getFocusPos() == FOCUS_TOP) {
                host.mediaTop.toNextSection();
            } else if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
                host.mediaMiddle.toNextSection();
            }
        } else if (CTRL_MEDIA_PREV_SECTION.equals(signal)) {
            if (focusView.getFocusPos() == FOCUS_TOP) {
                host.mediaTop.toPrevSection();
            } else if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
                host.mediaMiddle.toPrevSection();
            }
        }*/ else if (CTRL_FOCUS_NEXT.equals(signal)) {
            focusView.focusNext();
        } else if (CTRL_FOCUS_PREV.equals(signal)) {
            focusView.focusPrev();
        } else if (CTRL_FOCUS_TOP.equals(signal)) {
            focusView.focus(FOCUS_TOP);
        } else if (CTRL_FOCUS_MIDDLE.equals(signal)) {
            focusView.focus(FOCUS_MIDDLE);
        } else if (CTRL_FOCUS_BOTTOM.equals(signal)) {
            focusView.focus(FOCUS_BOTTOM);
        }
    }
    
    private void onGotLayoutPortStd() {
        if (rotate.isPort()) {
            layout.setPortCurrent(layout.port_std);
            layout.layoutAnimated(layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortFull() {
        if (focusView.getFocusPos() == FOCUS_TOP) {
            onGotLayoutPortTopFull();
        }
        if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            onGotLayoutPortMiddleFull();
        }
    }
    
    private void onGotLayoutPortHalf() {
        if (focusView.getFocusPos() == FOCUS_TOP) {
            onGotLayoutPortTopHalf();
        }
        if (focusView.getFocusPos() == FocusPosition.FOCUS_MIDDLE) {
            onGotLayoutPortMiddleHalf();
        }
    }
    
    private void onGotLayoutPortTopFull() {
        if (rotate.isPort()) {
            layout.setPortCurrent(layout.port_topFull);
            layout.layoutAnimated(layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortTopHalf() {
        if (rotate.isPort()) {
            layout.setPortCurrent(layout.port_topHalf);
            layout.layoutAnimated(layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortMiddleFull() {
        if (rotate.isPort()) {
            layout.setPortCurrent(layout.port_middleFull);
            layout.layoutAnimated(layout.getPortCurrent());
        }
    }
    
    private void onGotLayoutPortMiddleHalf() {
        if (rotate.isPort()) {
            layout.setPortCurrent(layout.port_middleHalf);
            layout.layoutAnimated(layout.getPortCurrent());
        }
    }
}