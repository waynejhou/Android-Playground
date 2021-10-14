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
import org.waynezhou.libUtil.event.EventHandler;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.event.EventListener;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;

final class Control {
    private Activity host;
    private Layout layout;
    private FocusView focusView;
    private Rotate rotate;
    private Control control;
    
    void init(Activity activity) {
        this.host = activity;
        layout = host.layout;
        focusView = host.focusView;
        rotate = host.rotate;
        control = host.control;
        
        //this.host.getEventGroup().on(g->g.pause, $->gSensor.Off());
        //this.host.getEventGroup().on(g->g.resume, $->gSensor.On());
        this.host.getEventGroup()
          .on(g -> g.create, this::onHostCreate)
          .on(g -> g.keyDown, this::onHostKeyDown)
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
    
    private void onHostKeyDown(Activity.KeyDownEventArgs e) {
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
        host.runOnUiThread(() -> gotSignal.invoke(signal));
    }
    
    private final EventHandler<ControlSignal> gotSignal = new EventHandler<>();
    
    EventHolder<ControlSignal>.ListenerToken onGotSignal(EventListener<ControlSignal> listener) {
        return gotSignal.on(listener);
    }
}