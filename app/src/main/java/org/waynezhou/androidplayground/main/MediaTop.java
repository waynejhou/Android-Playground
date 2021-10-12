package org.waynezhou.androidplayground.main;

import android.os.Environment;

import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libView.MediaView;

import java.io.IOException;

final class MediaTop {
    private Activity host;

    void init(Activity activity) {
        this.host = activity;
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
        host.binding.mainTopContainer.addView(view);
        try {
            view.configPrepareVideo(Environment.getExternalStorageDirectory() + "/DCIM/dummy video port.mp4")
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