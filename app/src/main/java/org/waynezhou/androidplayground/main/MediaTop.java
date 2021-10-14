package org.waynezhou.androidplayground.main;

import static org.waynezhou.androidplayground.main.ControlSignal.*;
import static org.waynezhou.androidplayground.main.FocusPosition.*;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;

import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.PermissionChecker;
import org.waynezhou.libView.MediaView;

import java.io.IOException;

final class MediaTop {
    private Activity host;
    private Layout layout;
    private Control control;
    private FocusView focusView;
    
    void init(Activity activity) {
        this.host = activity;
        this.host.getEventGroup().on(g -> g.create, this::onHostCreate);
        this.layout = host.layout;
        this.control = host.control;
        this.focusView = host.focusView;
    }
    
    private void onHostCreate(Bundle bundle) {
        control.onGotSignal(this::onControlGotSignal);
        PermissionChecker fileReadPermissionChecker = new PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE);
        fileReadPermissionChecker.getEventGroup().on(g -> g.permissionGranted, e -> {
            this.create();
        });
        fileReadPermissionChecker.fire();
    }
    
    private void onControlGotSignal(ControlSignal signal) {
        if(focusView.getFocusPos()!=FOCUS_TOP) return;
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
    
    void create() {
        view = new MediaView(host);
        layout.binding.mainTopContainer.addView(view);
        try {
            view.configPrepareVideo(Environment.getExternalStorageDirectory() + "/DCIM/dummy video port fix.mp4")
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