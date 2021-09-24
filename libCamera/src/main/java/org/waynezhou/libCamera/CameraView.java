package org.waynezhou.libCamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.LogHelper;

import java.io.IOException;

@SuppressLint("ViewConstructor")
public class CameraView  extends TextureView implements TextureView.SurfaceTextureListener {

    // region constructor
    private Camera camera;
    private SurfaceTexture texture;
    public CameraView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.texture = getSurfaceTexture();
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        LogHelper.i("CameraView onSurfaceTextureAvailable");
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        }
        catch (IOException ex){
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
