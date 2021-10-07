package org.waynezhou.libView;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.ThreadUtils;

import java.io.IOException;

public class MediaView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private final TextureView textureView;
    public final MediaPlayer player;

    public MediaView(Context context) {
        super(context);
        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(this);
        this.addView(textureView);
        this.addOnLayoutChangeListener(this::onLayoutChange);
        player = new MediaPlayer();
        resetPlayer();
    }

    private void resetPlayer() {
        player.reset();
        player.setOnPreparedListener(this::onPlayerPrepared);
        player.setOnCompletionListener(this::onPlayerCompletion);
        player.setOnSeekCompleteListener(this::onPlayerSeekComplete);
        isPlayerPrepared = false;
    }


    private volatile boolean isPlayerPrepared = false;
    @NonNull
    private Runnable onVideoPrepared = DelegateUtils.NothingRunnable;

    private void onPlayerPrepared(MediaPlayer mediaPlayer) {
        isPlayerPrepared = true;
        onVideoPrepared.run();
    }

    public boolean isPlayerPrepared() {
        return isPlayerPrepared;
    }

    @NonNull
    private Runnable onVideoCompletion = DelegateUtils.NothingRunnable;

    private void onPlayerCompletion(MediaPlayer mediaPlayer) {
        onVideoCompletion.run();
    }

    @NonNull
    private Runnable onceVideoSeekComplete = DelegateUtils.NothingRunnable;

    private void onPlayerSeekComplete(MediaPlayer mediaPlayer) {
        onceVideoSeekComplete.run();
        onceVideoSeekComplete = DelegateUtils.NothingRunnable;
    }

    public void setOnceVideoSeekComplete(@NonNull Runnable onceVideoSeekComplete) {
        this.onceVideoSeekComplete = onceVideoSeekComplete;
    }

    private void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //textureView.requestLayout();
        setTextureSize();
    }

    private volatile int lastWid = 0;
    private volatile int lastHei = 0;

    private void setTextureSize() {
        if (!isPlayerPrepared) return;
        if (lastWid == getWidth() && lastHei == getHeight()) return;
        lastWid = getWidth();
        lastHei = getHeight();
        //LogHelper.d("%d, %d", lastWid, lastHei);
        final float ratio = Math.min((float) getWidth() / (float) player.getVideoWidth(), (float) getHeight() / (float) player.getVideoHeight());
        //LogHelper.d("%d, %d",player.getVideoWidth(),  player.getVideoHeight());
        //LogHelper.d("%f, %f, %f", ratio, ratio * player.getVideoWidth(), ratio * player.getVideoHeight());
        textureView.getLayoutParams().width = Math.round(ratio * player.getVideoWidth());
        textureView.getLayoutParams().height = Math.round(ratio * player.getVideoHeight());
        ((LayoutParams) textureView.getLayoutParams()).gravity = Gravity.CENTER;
        post(textureView::requestLayout);
    }

    private volatile boolean isSurfaceTextureAvailable = false;

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        isSurfaceTextureAvailable = true;
        player.setSurface(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    private Section section;
    private Thread sectionTrackingThd;

    private void disposeTrackingThd() {
        if (sectionTrackingThd != null) {
            sectionTrackingThd.interrupt();
            sectionTrackingThd = null;
        }
    }

    public void setSection(Section section) {
        if (!isPlayerPrepared) throw new IllegalArgumentException();
        disposeTrackingThd();
        if (section == null) {
            this.section = null;
        } else {
            this.section = section;
            player.seekTo(section.start_msec);
            sectionTrackingThd = ThreadUtils.interval(33, () -> {
                if (Math.abs(player.getCurrentPosition() - this.section.end_msec) < 10) {
                    player.seekTo(this.section.start_msec);
                }
            });
            sectionTrackingThd.start();
        }
    }

    public static class Section {
        public final int fps;
        public final int startFrame;
        public final int endFrame;
        public final int start_msec, end_msec;
        public final boolean isLooping;

        public Section(int fps, int startFrame, int endFrame, boolean looping) {
            this.fps = fps;
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            this.start_msec = frame2millisecond(fps, startFrame);
            this.end_msec = frame2millisecond(fps, endFrame);
            this.isLooping = looping;
        }

        public static int frame2millisecond(int fps, int frame) {
            int result = (int) (((float) frame / fps) * 1000f);
            return Math.max(result, 0);
        }

        public static int second2Frame(int fps, long milliSecond) {
            return (int) ((milliSecond * fps) / 1000);
        }
    }

    public VideoConfiguration configPrepareVideo(String path) {
        return new VideoConfiguration(path);
    }

    public class VideoConfiguration {
        private final String path;
        private Runnable onPrepared = DelegateUtils.NothingRunnable;
        private Runnable onCompletion = DelegateUtils.NothingRunnable;

        protected VideoConfiguration(String path) {
            this.path = path;
        }

        public VideoConfiguration setOnCompletion(@NonNull Runnable onCompletion) {
            this.onCompletion = onCompletion;
            return this;
        }

        public VideoConfiguration setOnPrepared(@NonNull Runnable onPrepared) {
            this.onPrepared = onPrepared;
            return this;
        }

        private boolean looping;

        public VideoConfiguration setLooping(boolean looping) {
            this.looping = looping;
            return this;
        }

        public void prepare() throws IOException {
            resetPlayer();
            player.setDataSource(path);
            MediaView.this.onVideoPrepared = onPrepared;
            MediaView.this.onVideoCompletion = onCompletion;
            player.setLooping(looping);
            player.prepareAsync();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        disposeTrackingThd();
    }
}
