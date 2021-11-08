package org.waynezhou.libCamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.Linq;
import org.waynezhou.libUtil.LogHelper;

import java.io.IOException;

public class CameraView extends FrameLayout implements TextureView.SurfaceTextureListener {
    
    // region constructor
    public final Camera camera;
    private final Camera.Parameters camParams;
    private final TextureView textureView;
    private SurfaceTexture texture;
    
    public CameraView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        
        this.camParams = camera.getParameters();
        this.textureView = new TextureView(context);
        this.addView(textureView);
        this.addOnLayoutChangeListener(this::onLayoutChange);
        textureView.setSurfaceTextureListener(this);
    }
    
    public void setPreviewCallback(byte[] buffer, Camera.PreviewCallback callback){
        camera.addCallbackBuffer(buffer);
        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            private final byte[] mBuffer;
            {this.mBuffer = buffer;}
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                callback.onPreviewFrame(data, camera);
                camera.addCallbackBuffer(mBuffer);
            }
        });
    }
    

    
    private int cameraOrientation = 0;
    
    public int getCameraWidth() {
        return camParams.getPreviewSize().width;
    }
    
    public int getCameraHeight() {
        return camParams.getPreviewSize().height;
    }
    
    private int getInnerViewWidth() {
        if(cameraOrientation == 90 || cameraOrientation == 270){
            return getHeight();
        }
        return getWidth();
    }
    
    private int getInnerViewHeight() {
        if(cameraOrientation == 90 || cameraOrientation == 270){
            return getWidth() * getHeight()/getWidth();
        }
        return getHeight();
    }
    
    public void setCameraOrientation(int cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
        LogHelper.d(cameraOrientation);
        textureView.setRotation(cameraOrientation);
    }
    
    private void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        setTextureSize();
    }
    
    private volatile int lastWid = 0;
    private volatile int lastHei = 0;
    
    private void setTextureSize() {
        if (!isPreviewing) return;
        if (lastWid == getWidth() && lastHei == getHeight()) return;
        lastWid = getWidth();
        lastHei = getHeight();
        final float ratio = Math.min((float) getInnerViewWidth() / (float) getCameraWidth(), (float) getInnerViewHeight() / (float) getCameraHeight());
        textureView.getLayoutParams().width = Math.round(ratio * getCameraWidth());
        textureView.getLayoutParams().height = Math.round(ratio * getCameraHeight());
        ((LayoutParams) textureView.getLayoutParams()).gravity = Gravity.CENTER;
        post(()->{
            textureView.requestLayout();
            textureView.setRotation(cameraOrientation);
        });
    }
    
    private boolean isPreviewing = false;
    
    public boolean isPreviewing() {
        return isPreviewing;
    }
    
    public void startPreview() {
        if (isSurfaceTextureAvailable) {
            isPreviewing = true;
            camera.startPreview();
            setTextureSize();
        } else {
            onceSurfaceTextureAvailable = () -> {
                isPreviewing = true;
                camera.startPreview();
                setTextureSize();
            };
        }
    }
    
    public void stopPreview() {
        camera.stopPreview();
        isPreviewing = false;
    }
    
    private final boolean isSurfaceTextureAvailable = false;
    private Runnable onceSurfaceTextureAvailable = DelegateUtils.NothingRunnable;
    
    public boolean isSurfaceTextureAvailable() {
        return isSurfaceTextureAvailable;
    }
    
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            camera.setPreviewTexture(surfaceTexture);
            onceSurfaceTextureAvailable.run();
            onceSurfaceTextureAvailable = DelegateUtils.NothingRunnable;
        } catch (IOException ex) {
            LogHelper.e("Start Texture view failed", ex);
        }
    }
    
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
    
    }
    
    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }
    
    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
    
    }
    // endregion
    
    
}
