package org.waynezhou.libUtil.activity_register;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.eventArgs.ActivityResultEventArgs;

public class ActivityResultRegister {
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<ActivityResultEventArgs> result = new EventHolder<>();
        
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
    


    @NonNull
    private final ActivityResultLauncher<Object> launcher;

    protected ActivityResultRegister(final AppCompatActivity activity, final Intent intent) {
        this.invoker = eventGroup.getPrivateInvoker();
        launcher = activity.registerForActivityResult(
                new ActivityResultContract<Object, ActivityResultEventArgs>() {
                    @NonNull
                    @Override
                    public Intent createIntent(@NonNull Context context, Object input) {
                        return intent;
                    }

                    @Override
                    public ActivityResultEventArgs parseResult(int resultCode, @Nullable Intent intent) {
                        return new ActivityResultEventArgs(resultCode, intent);
                    }
                },
                (result -> invoker.invoke(g->g.result, result))
        );
    }

    public static class Builder {
        private Intent intent;

        public <TActivity extends AppCompatActivity> Builder activity(
                @NonNull Context context,
                @NonNull Class<TActivity> activityType) {
            intent = new Intent(context, activityType);
            return this;
        }

        public Builder action(
                @NonNull String action) {
            intent = new Intent(action);
            return this;
        }

        public Builder chooser(@NonNull Intent target, @NonNull String title) {
            intent = Intent.createChooser(target, title);
            return this;
        }

        public Builder configIntent(IntentConfiguration configuration) {
            configuration.config(intent);
            return this;
        }

        public ActivityResultRegister buildOn(AppCompatActivity activity) {
            return new ActivityResultRegister(activity, intent);
        }
    }
    
    public void fire() {
        launcher.launch(null);
    }

    @FunctionalInterface
    public interface IntentConfiguration {
        void config(Intent intent);
    }

}