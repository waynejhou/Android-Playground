package org.waynezhou.androidplayground.main;

import static android.content.Context.WINDOW_SERVICE;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import org.waynezhou.libCamera.CameraView;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.activity.ComponentizedActivity;
import org.waynezhou.libUtil.standard.StandardKt;
import org.waynezhou.libUtil.checker.PermissionChecker;

public class CameraTop extends ActivityComponent<MainActivity> {
    private Display windowDisplay;
    
    @Override
    protected void onInit() {
        WindowManager windowManager = (WindowManager) host.getSystemService(WINDOW_SERVICE);
        windowDisplay = windowManager.getDefaultDisplay();
    }
    
    @Override
    protected void onHostCreate(Bundle bundle) {
        rotate.onRotated(this::onHostRotated);
        this.create();
    }
    
    private void onHostRotated(Void unused) {
        setCameraViewRotation();
    }
    
    private void setCameraViewRotation() {
        if (rotate.isLand()) {
            if (windowDisplay.getRotation() == Surface.ROTATION_90) {
                cameraView.setCameraOrientation(0);
            } else {/*Surface.ROTATION_270*/
                cameraView.setCameraOrientation(180);
            }
        } else {
            if (windowDisplay.getRotation() == Surface.ROTATION_0) {
                cameraView.setCameraOrientation(90);
            } else {/*Surface.ROTATION_180*/
                cameraView.setCameraOrientation(270);
            }
        }
    }
    
    
    private CameraView cameraView;
    
    public void create() {
        StandardKt.apply(new PermissionChecker(host, true, Manifest.permission.CAMERA), it -> {
            it.getEvents()
              .on(g -> g.permissionGranted, e -> {
                  cameraView = new CameraView(host, Camera.open(0));
                  setCameraViewRotation();
                  layout.binding.mainTopContainer.addView(cameraView);
                  cameraView.startPreview();
              })
              .on(g -> g.permissionDenied, e -> {
                  LogHelper.i("Camera Permission Denied");
              })
            ;
        }).fire();
    }
}
