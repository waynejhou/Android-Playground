package org.waynezhou.libUtil;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.eventArgs.SensorAccuracyChangedEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorAdditionalInfoEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorChangedEventArgs;
import org.waynezhou.libUtil.eventArgs.SensorFlushCompletedEventArgs;
import org.waynezhou.libUtil.eventGroup.SensorToggleBaseEventGroup;

public class SensorToggle {

    private final _SensorToggleBaseEventGroup eventGroup = new _SensorToggleBaseEventGroup();
    private final BaseEventGroup<SensorToggleBaseEventGroup>.Invoker invoker;

    private static class _SensorToggleBaseEventGroup extends SensorToggleBaseEventGroup {
        @NonNull
        public BaseEventGroup<SensorToggleBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public SensorToggleBaseEventGroup getEventGroup() {
        return eventGroup;
    }


    private final SensorManager manager;
    private final Sensor sensor;
    private final int samplingPeriodUs;
    private final SensorEventCallback callback = new SensorEventCallback() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            invoker.invoke(g->g.changed, new SensorChangedEventArgs(event));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            invoker.invoke(g->g.accuracyChanged, new SensorAccuracyChangedEventArgs(sensor, accuracy));
        }

        @Override
        public void onFlushCompleted(Sensor sensor) {
            invoker.invoke(g->g.flushCompleted, new SensorFlushCompletedEventArgs(sensor));
        }

        @Override
        public void onSensorAdditionalInfo(SensorAdditionalInfo info) {
            invoker.invoke(g->g.additionalInfo, new SensorAdditionalInfoEventArgs(info));
        }
    };
    public SensorToggle(AppCompatActivity activity, int sensorType, int samplingPeriodUs) {
        this.manager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = this.manager.getDefaultSensor(sensorType);
        this.invoker = eventGroup.getInvoker();
        this.samplingPeriodUs = samplingPeriodUs;
    }
    public void on(){
        manager.registerListener(callback, sensor, samplingPeriodUs);
    }
    public void off(){
        manager.unregisterListener(callback);
    }
}
