package org.waynezhou.libUtil;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.eventArgs.ActivityResultEventArgs;
import org.waynezhou.libUtil.eventGroup.ActivityResultRegisterEventGroup;

public class ActivityResultRegister /*extends EventAction<ActivityResultRegister, ActivityResultRegisterEventGroup> */{

    private final _ActivityResultRegisterEventGroup eventGroup = new _ActivityResultRegisterEventGroup();
    private final EventGroup<ActivityResultRegisterEventGroup>.Invoker invoker;

    private static class _ActivityResultRegisterEventGroup extends ActivityResultRegisterEventGroup {
        public EventGroup<ActivityResultRegisterEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public ActivityResultRegisterEventGroup getEventGroup() {
        return eventGroup;
    }


    @NonNull
    private final ActivityResultLauncher<Object> launcher;

    protected ActivityResultRegister(final AppCompatActivity activity, final Intent intent) {
        //super(new ActivityResultRegisterEventGroup());
        this.invoker = eventGroup.getInvoker();
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
                (result -> {
                    invoker.invoke(g->g.result, result);
                    //this.eventPack.clear();
                })
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

    //@Override
    public void fire() {
        launcher.launch(null);
    }

    @FunctionalInterface
    public interface IntentConfiguration {
        void config(Intent intent);
    }

}