package org.waynezhou.androidplayground.main;

import static android.content.Context.WINDOW_SERVICE;

import android.Manifest;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;

import org.waynezhou.androidplayground.fragment.ObjectDetectionFragment;
import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.standard.StandardKt;
import org.waynezhou.libUtil.checker.PermissionChecker;

class ObjdetTop extends ActivityComponent<MainActivity> {
   
    private Display windowDisplay;
    private FragmentManager fragmentManager;
    
    @Override
    public void onHostCreate(Bundle bundle) {
        WindowManager windowManager = (WindowManager) host.getSystemService(WINDOW_SERVICE);
        windowDisplay = windowManager.getDefaultDisplay();
        fragmentManager = host.getSupportFragmentManager();
        host.onActivityRotated(this::onHostRotated);
        this.create();
    }
    
    private void onHostRotated(Void unused) {
        setCameraViewRotation();
    }
    
    private int getCameraViewRotation(){
        if(host.isOrientationLand()){
            if(windowDisplay.getRotation() == Surface.ROTATION_90){
                return 0;
            }else{/*Surface.ROTATION_270*/
                return 180;
            }
        }else{
            if(windowDisplay.getRotation() == Surface.ROTATION_0){
                return 90;
            }else{/*Surface.ROTATION_180*/
                return 270;
            }
        }
    }
    
    private void setCameraViewRotation() {
        objdetFragment.setCameraOrientation(getCameraViewRotation());
    }
    
    
    private ObjectDetectionFragment objdetFragment;
    private void create(){
        StandardKt.apply(new PermissionChecker(host, true, Manifest.permission.CAMERA), it->{
            it.getEvents()
              .on(g->g.permissionGranted, e->{
                  objdetFragment = new ObjectDetectionFragment(getCameraViewRotation());
                  fragmentManager.beginTransaction()
                    .add(host.getBinding().mainTopContainer.getId() ,objdetFragment, "objdet")
                    .commitNow();
                  objdetFragment.startPreview();
              })
              .on(g->g.permissionDenied, e->{
                  LogHelper.i("Camera Permission Denied");
              })
            ;
        }).fire();
    }
}
