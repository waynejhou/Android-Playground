package org.waynezhou.libUtil.toggle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.eventArgs.SensorAccuracyChangedEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorAdditionalInfoEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorFlushCompletedEventArgs;
import org.waynezhou.libUtil.eventGroup.SensorToggleBaseEventGroup;

public class SensorToggle {
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<SensorChangedEventArgs> changed = new EventHolder<>();
        @NonNull
        public final EventHolder<SensorAccuracyChangedEventArgs> accuracyChanged = new EventHolder<>();
        @NonNull
        public final EventHolder<SensorAdditionalInfoEventArgs> additionalInfo = new EventHolder<>();
        @NonNull
        public final EventHolder<SensorFlushCompletedEventArgs> flushCompleted = new EventHolder<>();
        
        @NonNull
        Invoker getPrivateInvoker() {
            return getInvoker();
        }
    }
    
    @NonNull
    private final EventGroup eventGroup = new EventGroup();
    
    @NonNull
    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }
    
    @NonNull
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    
    
    private final SensorManager manager;
    private final Sensor sensor;
    private final int samplingPeriodUs;
    private final SensorEventCallback callback = new SensorEventCallback() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            invoker.invoke(g -> g.changed, new SensorChangedEventArgs(event));
        }
        
        @Override
        public void onAccuracyChanged(
          Sensor sensor,
          int accuracy
        ) {
            invoker.invoke(g -> g.accuracyChanged, new SensorAccuracyChangedEventArgs(sensor, accuracy));
        }
        
        @Override
        public void onFlushCompleted(Sensor sensor) {
            invoker.invoke(g -> g.flushCompleted, new SensorFlushCompletedEventArgs(sensor));
        }
        
        @Override
        public void onSensorAdditionalInfo(SensorAdditionalInfo info) {
            invoker.invoke(g -> g.additionalInfo, new SensorAdditionalInfoEventArgs(info));
        }
    };
    
    public SensorToggle(
      AppCompatActivity activity,
      int sensorType,
      int samplingPeriodUs
    ) {
        this.manager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = this.manager.getDefaultSensor(sensorType);
        this.invoker = eventGroup.getPrivateInvoker();
        this.samplingPeriodUs = samplingPeriodUs;
    }
    
    public void on() {
        manager.registerListener(callback, sensor, samplingPeriodUs);
    }
    
    public void off() {
        manager.unregisterListener(callback);
    }
}
