package org.waynezhou.androidplayground.main;


import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.LayoutChangedReason.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.androidplayground.service.SingleBleDeviceService;
import org.waynezhou.libBluetooth.BtGuarantor;
import org.waynezhou.libUtil.BroadcastReceiverRegister;
import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.PermissionChecker;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SensorToggle;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;

public final class Activity extends AppCompatActivity {
    
    ActivityMainBinding binding;
    private SensorToggle gSensor;
    private GestureDetector gestureDetector;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogHelper.i();
        super.onCreate(savedInstanceState);
        
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
        
        layout.init(this);
        focusView.init(this);
        rotate.init(this);
        mediaTop.init(this);
        mediaMiddle.init(this);
        control.init(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(layout::onGlobalLayoutChanged);
        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(layout::onPreDraw);
        binding.getRoot().getViewTreeObserver().addOnDrawListener(layout::onRootDraw);
        layout.setChangedReason(CONTENT_VIEW_SET);
        layout.setContentViewSet(false);
        setContentView(binding.getRoot());
        
        gSensor = new SensorToggle(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
        //gSensor.getEventGroup().on(e -> e.changed, this::onGSensorValueChanged);
        
        PermissionChecker fileReadPermissionChecker = new PermissionChecker(this, true, Manifest.permission.READ_EXTERNAL_STORAGE);
        fileReadPermissionChecker.getEventGroup().on(g -> g.permissionGranted, e -> {
            mediaTop.create();
            mediaMiddle.create();
        });
        fileReadPermissionChecker.fire();
        
        binding.mainTopContainer.setOnClickListener(e -> control.sendSignal(CTRL_FOCUS_TOP));
        binding.mainMiddleContainer.setOnClickListener(e -> control.sendSignal(CTRL_FOCUS_MIDDLE));
        binding.mainBottomContainer.setOnClickListener(e -> control.sendSignal(CTRL_FOCUS_BOTTOM));
        
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
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
                if (binding.mainFocusView.getGlobalVisibleRect(focusBBox)) {
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
        binding.mainTouchArea.setOnTouchListener((v, e) -> {
            //LogHelper.i(MotionEventExplain.explainAction(e.getAction()));
            if (!gestureDetector.onTouchEvent(e)) {
                runOnUiThread(() -> binding.mainViewArea.dispatchTouchEvent(e));
            }
            return true;
        });
        
        BtGuarantor btGuarantor = new BtGuarantor(this);
        btGuarantor.getEventGroup()
          .on(g -> g.guaranteed, e -> {
              ComponentName name = startService(new Intent(this, SingleBleDeviceService.class));
              LogHelper.i(name);
          })
          .on(g -> g.notGuaranteed, e -> {
              LogHelper.e("Bluetooth error");
          });
        btGuarantor.fire();
    
        BroadcastReceiverRegister broadcastReceiverRegister = new BroadcastReceiverRegister();
        IntentFilter filter = new IntentFilter(SingleBleDeviceService.BROADCAST_GOT_CMD);
        broadcastReceiverRegister.setBroadReceive((ctx, intent)->{
            LogHelper.d(intent.getStringExtra("cmd"));
        });
        registerReceiver(broadcastReceiverRegister, filter);
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
    
    @Override
    protected void onDestroy() {
        LogHelper.i();
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        LogHelper.i();
        super.onResume();
        gSensor.On();
        hideSystemUI();
    }
    
    @Override
    protected void onPause() {
        LogHelper.i();
        super.onPause();
        gSensor.Off();
    }
    
    @Override
    protected void onStart() {
        LogHelper.i();
        Runnable a = () -> {
        };
        Runnable b = () -> {
        };
        
        LogHelper.d("a: %d; b: %d; a==b: %b; NothingRunnable: %d", a.hashCode(), b.hashCode(), a == b, DelegateUtils.NothingRunnable.hashCode());
        super.onStart();
    }
    
    @Override
    protected void onStop() {
        LogHelper.i();
        super.onStop();
        stopService(new Intent(this, SingleBleDeviceService.class));
    }
    
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        LogHelper.i();
        super.onPostCreate(savedInstanceState);
    }
    
    @Override
    protected void onPostResume() {
        LogHelper.i();
        super.onPostResume();
    }
    
    
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        LogHelper.i();
        LogHelper.d("new orientation %d", newConfig.orientation);
        if (rotate.isOrientationChanged(newConfig.orientation)) {
            control.sendSignal(CTRL_ROTATE);
        }
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogHelper.d(keyCode);
        if (keyCode == KeyEvent.KEYCODE_1) {
            control.sendSignal(CTRL_LAYOUT_PORT_STD);
        }
        if (keyCode == KeyEvent.KEYCODE_2) {
            control.sendSignal(CTRL_LAYOUT_PORT_HALF);
        }
        if (keyCode == KeyEvent.KEYCODE_3) {
            control.sendSignal(CTRL_LAYOUT_PORT_FULL);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            control.sendSignal(CTRL_MEDIA_PREV_SECTION);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            control.sendSignal(CTRL_MEDIA_NEXT_SECTION);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            control.sendSignal(CTRL_FOCUS_PREV);
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            control.sendSignal(CTRL_FOCUS_NEXT);
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = //View.SYSTEM_UI_FLAG_LOW_PROFILE
          View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    
    final Layout layout = new Layout();
    final Rotate rotate = new Rotate();
    final FocusView focusView = new FocusView();
    final MediaMiddle mediaMiddle = new MediaMiddle();
    final MediaTop mediaTop = new MediaTop();
    final Control control = new Control();
}
