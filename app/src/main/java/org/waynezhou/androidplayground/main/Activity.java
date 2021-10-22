package org.waynezhou.androidplayground.main;


import android.os.Bundle;

import androidx.annotation.Nullable;

import org.waynezhou.libView.AppCompatActivityWrapper;

public final class Activity extends AppCompatActivityWrapper {
    // region activity components
    final Layout layout = new Layout();
    final Rotate rotate = new Rotate();
    final FocusView focusView = new FocusView();
    final MediaMiddle mediaMiddle = new MediaMiddle();
    //final MediaTop mediaTop = new MediaTop();
    final Control control = new Control();
    //final BleControl bleControl = new BleControl();
    //final CameraTop cameraTop = new CameraTop();
    final ObjdetTop objdetTop = new ObjdetTop();
    @Override
    protected void onInitComponents(@Nullable Bundle savedInstanceState) {
        layout.init(this);
        control.init(this);
        focusView.init(this);
        rotate.init(this, Rotate.AutoRotationSource.System);
        //mediaTop.init(this);
        mediaMiddle.init(this);
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
