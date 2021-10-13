package org.waynezhou.androidplayground.main;

import static android.content.Context.WINDOW_SERVICE;
import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_ROTATE;

import android.Manifest;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;

import org.waynezhou.androidplayground.fragment.ObjectDetectionFragment;
import org.waynezhou.libCamera.CameraView;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.PermissionChecker;
import org.waynezhou.libUtil.StandardKt;

public class ObjdetTop {
    private Activity host;
    private Layout layout;
    private Rotate rotate;
    private Display windowDisplay;
    private FragmentManager fragmentManager;
    public void init(Activity activity){
        this.host = activity;
        this.layout = host.layout;
        this.rotate = host.rotate;
        host.getEventGroup().on(g->g.create, this::onHostCreate);
        
        WindowManager windowManager = (WindowManager) host.getSystemService(WINDOW_SERVICE);
        windowDisplay = windowManager.getDefaultDisplay();
    
        fragmentManager = host.getSupportFragmentManager();
    }
    
    private void onHostCreate(Bundle bundle) {
        rotate.onRotated(this::onHostRotated);
        this.create();
    }
    
    private void onHostRotated(Void unused) {
        setCameraViewRotation();
    }
    
    private int getCameraViewRotation(){
        if(rotate.isLand()){
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
    public void create(){
        StandardKt.apply(new PermissionChecker(host, true, Manifest.permission.CAMERA), it->{
            it.getEventGroup()
              .on(g->g.permissionGranted, e->{
                  objdetFragment = new ObjectDetectionFragment(getCameraViewRotation());
                  fragmentManager.beginTransaction()
                    .add(layout.binding.mainTopContainer.getId() ,objdetFragment, "objdet")
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
