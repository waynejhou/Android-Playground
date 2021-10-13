package org.waynezhou.androidplayground.model;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.Integer;
import java.lang.Object;
import java.util.HashMap;
import java.util.Map;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.metadata.MetadataExtractor;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public final class ObjectDetectionFloat16YoloV4 {
    @NonNull
    private final Model model;
    
    private ObjectDetectionFloat16YoloV4(@NonNull Context context, @NonNull Model.Options options)
      throws IOException {
        model = Model.createModel(context, "object_detection_float16_Yolov4.tflite", options);
        MetadataExtractor extractor = new MetadataExtractor(model.getData());
    }
    
    @NonNull
    public static ObjectDetectionFloat16YoloV4 newInstance(@NonNull Context context) throws IOException {
        return new ObjectDetectionFloat16YoloV4(context, (new Model.Options.Builder()).build());
    }
    
    @NonNull
    public static ObjectDetectionFloat16YoloV4 newInstance(@NonNull Context context,@NonNull Model.Options options) throws IOException {
        return new ObjectDetectionFloat16YoloV4(context, options);
    }
    
    @NonNull
    public Outputs process(@NonNull TensorBuffer inputFeature0) {
        Outputs outputs = new Outputs(model);
        model.run(new Object[]{inputFeature0.getBuffer()}, outputs.getBuffer());
        return outputs;
    }
    
    public void close() {
        model.close();
    }
    
    public static class Outputs {
        private final TensorBuffer outputFeature0;
        
        private Outputs(Model model) {
            this.outputFeature0 = TensorBuffer.createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32);
        }
        
        @NonNull
        public TensorBuffer getOutputFeature0AsTensorBuffer() {
            return outputFeature0;
        }
        
        @NonNull
        private Map<Integer, Object> getBuffer() {
            Map<Integer, Object> outputs = new HashMap<>();
            outputs.put(0, outputFeature0.getBuffer());
            return outputs;
        }
    }
}
