package org.waynezhou.libView;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.ThreadUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        player.setOnErrorListener(this::onPlayerError);
        player.setOnInfoListener(this::onPlayerInfo);
        isPlayerPrepared = false;
    }
    
    private volatile boolean isPlayerPrepared = false;
    @NonNull
    private Runnable onVideoPrepared = DelegateUtils.NothingRunnable;
    
    private void onPlayerPrepared(MediaPlayer mediaPlayer) {
        isPlayerPrepared = true;
        if(isSurfaceTextureAvailable){
            onVideoPrepared.run();
        }else{
            onceSurfaceTextureAvailable = ()->{
                onVideoPrepared.run();
            };
        }
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
    
    private boolean onPlayerInfo(MediaPlayer mp, int what, int extra) {
        //LogHelper.i("%s, Extra: %d", explainMediaPlayerInfoWhat(what), extra);
        return false;
    }
    
    private boolean onPlayerError(MediaPlayer mp, int what, int extra) {
        //LogHelper.e("%s, %s", explainMediaPlayerErrorWhat(what), explainMediaPlayerExtraWhat(extra));
        return false;
    }
    
    private static final Map<Integer, String> mediaPlayerInfoWhatExplain = new HashMap<Integer, String>() {{
        put(/*  1*/MediaPlayer.MEDIA_INFO_UNKNOWN, "Unknown: Unspecified media player info.");
        put(/*700*/MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING, "Video track lagging: The video is too complex for the decoder: it can't decode frames fast enough. Possibly only the audio plays fine at this stage.");
        put(/*  3*/MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START, "Rendering start: The player just pushed the very first video frame for rendering.");
        put(/*701*/MediaPlayer.MEDIA_INFO_BUFFERING_START, "Buffering start: MediaPlayer is temporarily pausing playback internally in order to buffer more data.");
        put(/*702*/MediaPlayer.MEDIA_INFO_BUFFERING_END, "Buffering end: MediaPlayer is resuming playback after filling buffers.");
        put(703/* MediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH*/, "Network bandwidth: Estimated network bandwidth information (kbps) is available; currently this event fires simultaneously as MEDIA_INFO_BUFFERING_START and MEDIA_INFO_BUFFERING_END when playing network files.");
        put(/*800*/MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING, "Bad interleaving: Bad interleaving means that a media has been improperly interleaved or not interleaved at all, e.g has all the video samples first then all the audio ones. Video is playing but a lot of disk seeks may be happening.");
        put(/*801*/MediaPlayer.MEDIA_INFO_NOT_SEEKABLE, "Not seekable: The media cannot be seeked (e.g live stream)");
        put(/*802*/MediaPlayer.MEDIA_INFO_METADATA_UPDATE, "Metadata update: A new set of metadata is available.");
        put(/*901*/MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE, "Unsupported subtitle: Subtitle track was not supported by the media framework.");
        put(/*902*/MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT, "Subtitle timed out: Reading the subtitle track takes too long.");
    }};
    
    public static String explainMediaPlayerInfoWhat(int action) {
        if (mediaPlayerInfoWhatExplain.containsKey(action)) {
            return String.format(Locale.TRADITIONAL_CHINESE,
              "Info What Code: %d, Explain: %s"
              , action, mediaPlayerInfoWhatExplain.get(action));
        }
        return String.format(Locale.TRADITIONAL_CHINESE,
          "Info What Code: %d, Explain: Unknown Info What"
          , action);
    }
    
    private static final Map<Integer, String> mediaPlayerErrorWhatExplain = new HashMap<Integer, String>() {{
        put(/*  1*/MediaPlayer.MEDIA_ERROR_UNKNOWN, "Unknown: Unspecified media player error.");
        put(/*100*/MediaPlayer.MEDIA_ERROR_SERVER_DIED, "Server died: Media server died. In this case, the application must release the MediaPlayer object and instantiate a new one.");
    }};
    
    public static String explainMediaPlayerErrorWhat(int action) {
        if (mediaPlayerErrorWhatExplain.containsKey(action)) {
            return String.format(Locale.TRADITIONAL_CHINESE,
              "Error What Code: %d, Explain: %s"
              , action, mediaPlayerErrorWhatExplain.get(action));
        }
        return String.format(Locale.TRADITIONAL_CHINESE,
          "Error What Code: %d, Explain: Unknown Error What"
          , action);
    }
    
    private static final Map<Integer, String> mediaPlayerErrorExtraExplain = new HashMap<Integer, String>() {{

        put(/*-1004*/MediaPlayer.MEDIA_ERROR_IO, "IO: File or network related operation errors.");
        put(/*-1007*/MediaPlayer.MEDIA_ERROR_MALFORMED, "Malformed: Bitstream is not conforming to the related coding standard or file spec.");
        put(/*-1010*/MediaPlayer.MEDIA_ERROR_UNSUPPORTED, "Unsupported: Bitstream is conforming to the related coding standard or file spec, but the media framework does not support the feature.");
        put(/* -110*/MediaPlayer.MEDIA_ERROR_TIMED_OUT, "Timed out: Some operation takes too long to complete, usually more than 3-5 seconds.");
        put(-2147483648/*MediaPlayer.MEDIA_ERROR_SYSTEM*/, "System: Unspecified low-level system error. This value originated from UNKNOWN_ERROR in system/core/include/utils/Errors.h");
    }};
    
    public static String explainMediaPlayerExtraWhat(int action) {
        if (mediaPlayerErrorExtraExplain.containsKey(action)) {
            return String.format(Locale.TRADITIONAL_CHINESE,
              "Error Extra Code: %d, Explain: %s"
              , action, mediaPlayerErrorExtraExplain.get(action));
        }
        return String.format(Locale.TRADITIONAL_CHINESE,
          "Error Extra Code: %d, Explain: Unknown Error Extra"
          , action);
    }
    
    private void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //textureView.requestLayout();
        setTextureSize();
    }
    
    private volatile int lastWid = 0;
    private volatile int lastHei = 0;
    
    private void setTextureSize() {
        //LogHelper.i();
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
    private Runnable onceSurfaceTextureAvailable = DelegateUtils.NothingRunnable;
    public boolean isSurfaceTextureAvailable() {
        return isSurfaceTextureAvailable;
    }
    
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        isSurfaceTextureAvailable = true;
        player.setSurface(new Surface(surface));
        onceSurfaceTextureAvailable.run();
        onceSurfaceTextureAvailable = DelegateUtils.NothingRunnable;
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
