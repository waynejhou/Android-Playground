package org.waynezhou.androidplayground.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.waynezhou.androidplayground.databinding.FragmentObjectDetectionBinding;
import org.waynezhou.androidplayground.model.ObjectDetectionFloat16YoloV4;
import org.waynezhou.libCamera.CameraView;
import org.waynezhou.libUtil.DelegateUtils;
import org.waynezhou.libUtil.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectDetectionFragment extends Fragment
{
    private final int initialRotation;
    public ObjectDetectionFragment(int initialRotation){
        this.initialRotation = initialRotation;
    }
    
    private FragmentObjectDetectionBinding binding;
    private CameraView cameraView;
    private final byte[] cameraCallbackBuffer = new byte[1024 * 1024 * 10];
    private ObjectDetectionFloat16YoloV4 model;
    private Bitmap rgbFrameBitmap ;
    private Bitmap croppedBitmap ;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding==null) binding = FragmentObjectDetectionBinding.inflate(inflater, container, false);
        cameraView = new CameraView(this.getActivity(), Camera.open(0));
        cameraView.setCameraOrientation(initialRotation);
        cameraView.setPreviewCallback(cameraCallbackBuffer, this::cameraPreviewCallback);
        binding.getRoot().addView(cameraView);
        try {
            model = ObjectDetectionFloat16YoloV4.newInstance(requireActivity());
        } catch (IOException e) {
            LogHelper.e("model init error", e);
        }
        rgbFrameBitmap = Bitmap.createBitmap(cameraView.getCameraWidth(), cameraView.getCameraHeight(), Bitmap.Config.ARGB_8888);
        croppedBitmap= Bitmap.createBitmap(416, 416, Bitmap.Config.ARGB_8888);
        frameToCropTransform =
          ImageUtils.getTransformationMatrix(
            cameraView.getCameraWidth(), cameraView.getCameraHeight(),
            INPUT_SIZE, INPUT_SIZE,
            0, false);
    
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
        return binding.getRoot();
    }
    
    ImageProcessor imageProcessor = new ImageProcessor.Builder()
      //.add(new ResizeWithCropOrPadOp(cropSize,cropSize))
      .add(new ResizeOp(INPUT_SIZE,INPUT_SIZE,ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
      //.add(new Rot90Op(sensorOrientation))
      .add(new NormalizeOp(0f, 1f))
      .build();
    
    private void cameraPreviewCallback(byte[] bytes, Camera camera) {
        LogHelper.d(bytes.length);
        int[] rgbBytes = new int[ cameraView.getCameraWidth() * cameraView.getCameraHeight()];
        ImageUtils.convertYUV420SPToARGB8888(bytes, cameraView.getCameraWidth(), cameraView.getCameraHeight(), rgbBytes);
        rgbFrameBitmap.setPixels(rgbBytes, 0, cameraView.getCameraWidth(), 0, 0, cameraView.getCameraWidth(), cameraView.getCameraHeight());
        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(croppedBitmap);
        
        ArrayList<Recognition> detections = new ArrayList<Recognition>();
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, new float[1][OUTPUT_WIDTH_TINY[0]][4]);
        outputMap.put(1, new float[1][OUTPUT_WIDTH_TINY[1]][labels.size()]);
    }
    
    protected static final int BATCH_SIZE = 1;
    protected static final int PIXEL_SIZE = 3;
    private static final int INPUT_SIZE = 416;
    private static final int[] OUTPUT_WIDTH_TINY = new int[]{2535, 2535};
    private static final String[] labels = new String[]{"A", "B"};
    protected ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }
    
    private boolean isViewCreated = false;
    private Runnable onceViewCreated = DelegateUtils.NothingRunnable;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        isViewCreated = true;
        onceViewCreated.run();
        onceViewCreated = DelegateUtils.NothingRunnable;
    }
    
    public void startPreview() {
        if(isViewCreated){
            cameraView.startPreview();
        }else{
            onceViewCreated = ()->{
                cameraView.startPreview();
            };
        }
    }
    
    public void setCameraOrientation(int cameraViewRotation) {
        cameraView.setCameraOrientation(cameraViewRotation);
    }
}
