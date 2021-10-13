package org.waynezhou.androidplayground.main;


import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.LayoutChangedReason.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.libBluetooth.BtGuarantor;
import org.waynezhou.libUtil.BroadcastReceiverRegister;
import org.waynezhou.libUtil.PermissionChecker;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.SensorToggle;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libView.AppCompatActivityWrapper;
import org.waynezhou.libView.MotionEventExplain;

public final class Activity extends AppCompatActivityWrapper {
    // region activity components
    final Layout layout = new Layout();
    final Rotate rotate = new Rotate();
    final FocusView focusView = new FocusView();
    //final MediaMiddle mediaMiddle = new MediaMiddle();
    //final MediaTop mediaTop = new MediaTop();
    final Control control = new Control();
    //final BleControl bleControl = new BleControl();
    //final CameraTop cameraTop = new CameraTop();
    final ObjdetTop objdetTop = new ObjdetTop();
    @Override
    protected void onInitComponents(@Nullable Bundle savedInstanceState) {
        layout.init(this);
        focusView.init(this);
        rotate.init(this);
        //mediaTop.init(this);
        //mediaMiddle.init(this);
        control.init(this);
        //bleControl.init(this);
        //cameraTop.init(this);
        objdetTop.init(this);
    }
    // endregion
    
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
