package org.waynezhou.libBluetooth.bt.enabler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.register.ActivityResultRegister;
import org.waynezhou.libUtil.event.BaseEventGroup;

public final class BtEnabler {
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<Void> agree = new EventHolder<>();
        @NonNull
        public final EventHolder<Void> disagree = new EventHolder<>();
        
        @NonNull
        Invoker getPrivateInvoker() {
            return getInvoker();
        }
    }
    
    @NonNull
    private final EventGroup eventGroup = new EventGroup();
    
    @NonNull
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    
    @NonNull
    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }

    @NonNull
    private final ActivityResultRegister register;

    public BtEnabler(AppCompatActivity activity) {
        this.invoker = eventGroup.getPrivateInvoker();
        this.register = new ActivityResultRegister.Builder()
                .action(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                .buildOn(activity);
    }

    public void fire() {
        register.getEvents()
                .on(g->g.result, e -> {
                    if (e.resultCode == Activity.RESULT_OK) {
                        invoker.invoke(g->g.agree, null);
                    } else {
                        invoker.invoke(g->g.disagree, null);
                    }
                });
        register.fire();
    }
}
