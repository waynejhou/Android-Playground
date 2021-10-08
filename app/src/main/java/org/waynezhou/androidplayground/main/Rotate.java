package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.LayoutChangedReason.*;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import org.waynezhou.libUtil.LogHelper;

final class Rotate {
    private Activity host;
    private Integer orientation = null;
    
    void init(Activity activity) {
        host = activity;
        orientation = host.getResources().getConfiguration().orientation;
    }
    
    Integer getOrientation() {
        return orientation;
    }
    
    boolean isOrientationChanged(int orientation){
        return !this.orientation.equals(orientation);
    }
    
    boolean isLand() {
        return orientation != null && orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    boolean isPort() {
        return orientation != null && orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    boolean isNotLand() {
        return orientation == null || orientation != Configuration.ORIENTATION_LANDSCAPE;
    }
    
    boolean isNotPort() {
        return orientation == null || orientation != Configuration.ORIENTATION_PORTRAIT;
    }
    
    void auto() {
        if (isLand()) {
            _toPort();
        } else if (isPort()) {
            _toLand();
        }
    }
    void to(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            _toLand();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            _toPort();
        }
    }
    
    void toLand() {
        if (isLand()) {
            return;
        }
        _toLand();
    }
    
    void toPort() {
        if (isPort()) {
            return;
        }
        _toPort();
    }
    
    private void _toLand() {
        LogHelper.i();
        orientation = Configuration.ORIENTATION_LANDSCAPE;
        host.layout.getPortCurrent().transpose().runWithoutAnimation(false);
        host.layout.setOnceRotationRequested(() -> {
            host.layout.layoutAnimated(host.layout.getLandCurrent());
        });
        host.layout.setChangedReason(ROTATION_REQUESTED);
        //host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    @SuppressLint("SourceLockedOrientationActivity")
    private void _toPort() {
        orientation = Configuration.ORIENTATION_PORTRAIT;
        host.layout.getLandCurrent().transpose().runWithoutAnimation(false);
        host.layout.setOnceRotationRequested(() -> {
            host.layout.layoutAnimated(host.layout.getPortCurrent());
        });
        host.layout.setChangedReason(ROTATION_REQUESTED);
        //host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
