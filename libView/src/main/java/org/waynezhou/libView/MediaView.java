package org.waynezhou.libView;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.StandardKt;

import java.io.IOException;
import java.io.InputStream;

public class MediaView extends SurfaceView {
    private MediaPlayer mediaPlayer;
    private final SurfaceHolder holder;
    private final Thread thread;
    private boolean looping = false;
    private PositionChangedListener positionChangedListener = timestamp -> {
    };
    private MediaPlayer.OnCompletionListener completionListener = (e) -> {
    };
    private MediaPlayer.OnPreparedListener preparedListener = (e) -> {
    };
    private MediaPlayer.OnInfoListener infoListene = (MediaPlayer mp, int what, int extra) -> {
        return false;
    };
    private MediaPlayer.OnErrorListener errorListener = (MediaPlayer mp, int what, int extra) -> {
        return false;
    };
    private StandardKt.RunBlock<MediaPlayer> dataSourceRunBlock = (it) -> {
    };
    private final SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        int position = 0;

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            LogHelper.i("media view surfaceCreated");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnInfoListener((MediaPlayer mp, int what, int extra) -> {
                LogHelper.d("MediaView OnInfoListener what: " + what + " extra: " + extra);
                return true;
            });
            mediaPlayer.setOnPreparedListener(e -> {
                LogHelper.d("setOnPreparedListener");
                mediaPlayer.seekTo(position);
            });
            mediaPlayer.setOnCompletionListener(completionListener);
            mediaPlayer.setOnErrorListener(errorListener);
            mediaPlayer.setLooping(looping);
            mediaPlayer.setOnSeekCompleteListener(e -> {
                LogHelper.d("Seek Complete");
                if (position != 0) {
                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.start();
                } else {
                    mediaPlayer.setDisplay(holder);
                    preparedListener.onPrepared(e);
                }
            });
            dataSourceRunBlock.run(mediaPlayer);
            mediaPlayer.prepareAsync();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            LogHelper.i("media view surfaceChanged");
            LogHelper.d(mediaPlayer.isPlaying());
            //mediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            LogHelper.i("media view surfaceDestroyed");
            //mediaPlayer.setDisplay(null);
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    };

    public MediaView(Context context) {
        super(context);
        this.holder = this.getHolder();
        holder.addCallback(callback);
        thread = new Thread(this::invokeMediaTimestamp);
        thread.start();
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.holder = this.getHolder();
        holder.addCallback(callback);
        thread = new Thread(this::invokeMediaTimestamp);
        thread.start();
    }

    public MediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.holder = this.getHolder();
        holder.addCallback(callback);
        thread = new Thread(this::invokeMediaTimestamp);
        thread.start();
    }

    public MediaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.holder = this.getHolder();
        holder.addCallback(callback);
        thread = new Thread(this::invokeMediaTimestamp);
        thread.start();
    }


    private void invokeMediaTimestamp() {
        while (true) {
            try {
                Thread.sleep(500);
                if (mediaPlayer != null) {
                    MediaTimestamp timestamp = mediaPlayer.getTimestamp();
                    if (timestamp != null) {
                        positionChangedListener.run(timestamp);
                    }
                }
            } catch (InterruptedException e) {
                LogHelper.e("Video View Error", e);
            }
        }
    }

    // region load
    public MediaView loadFromAssets(AppCompatActivity activity, String path) {
        dataSourceRunBlock = (mp) -> {
            try {
                mediaPlayer.setDataSource(new InputStreamMediaDataSource(activity.getAssets().open(path)));
            } catch (IOException e) {
                LogHelper.e("Loading Data Source Error", e);
            }
        };
        return this;
    }

    public MediaView loadFromPath(String path) {
        dataSourceRunBlock = (mp) -> {
            try {
                mediaPlayer.setDataSource(path);
            } catch (IOException e) {
                LogHelper.e("Loading Data Source Error", e);
            }
        };
        return this;
    }

    public MediaView loadFromInputStream(InputStream stream) {
        dataSourceRunBlock = (mp) -> {
            mediaPlayer.setDataSource(new InputStreamMediaDataSource(stream));
        };
        return this;
    }
    // endregion load

    public MediaView setLooping(boolean looping) {
        this.looping = looping;
        return this;
    }

    public MediaView setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        preparedListener = listener;
        return this;
    }

    public MediaView setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        completionListener = listener;
        return this;
    }

    public MediaView setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        errorListener = listener;
        return this;
    }

    public MediaView setOnPositionChanged(PositionChangedListener listener) {
        this.positionChangedListener = listener;
        return this;
    }

    public MediaView start() {
        mediaPlayer.start();
        return this;
    }

    public MediaView pause() {
        mediaPlayer.pause();
        return this;
    }

    public void release() {
        mediaPlayer.release();
    }

    @FunctionalInterface
    public interface PositionChangedListener {
        void run(MediaTimestamp timestamp);
    }

    @FunctionalInterface
    public interface ReadyToStartListener {
        void readyToStart();
    }
}
