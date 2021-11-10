package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_MEDIA_NEXT_SECTION;
import static org.waynezhou.androidplayground.main.ControlSignal.CTRL_MEDIA_PREV_SECTION;
import static org.waynezhou.androidplayground.main.FocusPosition.FOCUS_MIDDLE;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;

import org.waynezhou.libUtil.activity.ActivityComponent;
import org.waynezhou.libUtil.log.LogHelper;
import org.waynezhou.libUtil.checker.PermissionChecker;
import org.waynezhou.libView.MediaView;

import java.io.IOException;

final class MediaMiddle extends ActivityComponent<MainActivity> {
 
    @Override
    public void onHostCreate(Bundle bundle) {
        host.onControlGotSignal(this::onControlGotSignal);
        PermissionChecker fileReadPermissionChecker = new PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE);
        fileReadPermissionChecker.getEvents()
          .on(g -> g.permissionGranted, e -> {
              this.create();
          });
        fileReadPermissionChecker.fire();
    }
    
    private void onControlGotSignal(ControlSignal signal) {
        if (host.getFocusViewPos() != FOCUS_MIDDLE) return;
        if (CTRL_MEDIA_NEXT_SECTION.equals(signal)) {
            toNextSection();
        } else if (CTRL_MEDIA_PREV_SECTION.equals(signal)) {
            toPrevSection();
        }
    }
    
    private final MediaView.Section[] sections = new MediaView.Section[]{
      new MediaView.Section(30, 0, 1000, true),
      new MediaView.Section(30, 1000, 2000, true),
      new MediaView.Section(30, 2000, 3000, true),
      new MediaView.Section(30, 3000, 4000, true),
      new MediaView.Section(30, 4000, 5000, true),
      new MediaView.Section(30, 5000, 6000, true),
      new MediaView.Section(30, 6000, 7000, true),
      new MediaView.Section(30, 7000, 8000, true),
    };
    private int sectionIdx = 0;
    
    private MediaView view;
    
    private void create() {
        view = new MediaView(host);
        host.getBinding().mainMiddleContainer.addView(view);
        try {
            view.configPrepareVideo(Environment.getExternalStorageDirectory() + "/DCIM/dummy video fix.mp4")
              .setOnPrepared(() -> {
                  view.setOnceVideoSeekComplete(() -> {
                      view.player.start();
                  });
                  view.setSection(sections[sectionIdx]);
              })
              .setLooping(true)
              .prepare();
        } catch (IOException ex) {
            LogHelper.e(ex);
        }
    }
    
    void toNextSection() {
        if (view.isPlayerPrepared()) {
            sectionIdx = (sectionIdx + 1) % sections.length;
            view.setSection(sections[sectionIdx]);
        }
    }
    
    void toPrevSection() {
        if (view.isPlayerPrepared()) {
            sectionIdx = (sections.length + sectionIdx - 1) % sections.length;
            view.setSection(sections[sectionIdx]);
        }
    }
}
