package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.waynezhou.libUtil.activity.KeyDownEventArgs;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.event.EventHandler;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.event.EventListener;
import org.waynezhou.libUtil.standard.StandardKt;

interface IControl{
    void sendControlSignal(ControlSignal signal);
}

final class Control extends ActivityComponent<MainActivity> implements IControl {
    
    private GestureDetector gestureDetector;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onHostCreate(Bundle bundle) {
        this.host.getEvents().on(g -> g.keyDown, this::onHostKeyDown);
    
        StandardKt.apply(host.getBinding(), binding->{
            binding.mainTopContainer.setOnClickListener(e -> sendControlSignal(CTRL_FOCUS_TOP));
            binding.mainMiddleContainer.setOnClickListener(e -> sendControlSignal(CTRL_FOCUS_MIDDLE));
            binding.mainBottomContainer.setOnClickListener(e -> sendControlSignal(CTRL_FOCUS_BOTTOM));
        });
        
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
                if (host.getBinding().mainFocusView.getGlobalVisibleRect(focusBBox)) {
                    LogHelper.i("In focus view: %b %b", focusBBox.contains(x, y), host.isCurrentLayout(Layouts.tag_port, Layouts.tag_std));
                    if (focusBBox.contains(x, y)) {
                        if (Math.abs(velocityX) > Math.abs(velocityY)) {
                            if (velocityX > 1000) {
                                sendControlSignal(CTRL_MEDIA_PREV_SECTION);
                            }
                            if (velocityX < -1000) {
                                sendControlSignal(CTRL_MEDIA_NEXT_SECTION);
                            }
                        } else {
                            if (velocityY > 2000) {
                                if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_std))
                                    sendControlSignal(CTRL_LAYOUT_PORT_HALF);
                                else if (host.isCurrentLayout(Layouts.tag_port, Layouts.tag_half))
                                    sendControlSignal(CTRL_LAYOUT_PORT_FULL);
                            }
                            if (velocityY < -2000) {
                                sendControlSignal(CTRL_LAYOUT_PORT_STD);
                            }
                        }
                    }
                }
                return true;
            }
        });
    
        host.getBinding().mainTouchArea.setOnTouchListener((v, e) -> {
            if (!gestureDetector.onTouchEvent(e)) {
                host.runOnUiThread(() -> host.getBinding().mainViewArea.dispatchTouchEvent(e));
            }
            return true;
        });
    }
    
    private void onHostKeyDown(KeyDownEventArgs e) {
        LogHelper.d(e.keyCode);
        if (e.keyCode == KeyEvent.KEYCODE_1) {
            sendControlSignal(CTRL_LAYOUT_PORT_STD);
        }
        if (e.keyCode == KeyEvent.KEYCODE_2) {
            sendControlSignal(CTRL_LAYOUT_PORT_HALF);
        }
        if (e.keyCode == KeyEvent.KEYCODE_3) {
            sendControlSignal(CTRL_LAYOUT_PORT_FULL);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            sendControlSignal(CTRL_MEDIA_PREV_SECTION);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            sendControlSignal(CTRL_MEDIA_NEXT_SECTION);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            sendControlSignal(CTRL_FOCUS_PREV);
        }
        if (e.keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            sendControlSignal(CTRL_FOCUS_NEXT);
        }
    }
    
    @Override
    public void sendControlSignal(ControlSignal signal) {
        host.runOnUiThread(() -> gotSignal.invoke(signal));
    }
    
    private final EventHandler<ControlSignal> gotSignal = new EventHandler<>();
    
    EventHolder<ControlSignal>.ListenerToken onGotSignal(EventListener<ControlSignal> listener) {
        return gotSignal.on(listener);
    }
}