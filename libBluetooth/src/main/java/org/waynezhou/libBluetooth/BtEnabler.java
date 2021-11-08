package org.waynezhou.libBluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.register.ActivityResultRegister;
import org.waynezhou.libUtil.event.BaseEventGroup;

public final class BtEnabler {
    private final _BtEnablerBaseEventGroup eventGroup = new _BtEnablerBaseEventGroup();
    private final BaseEventGroup<BtEnablerBaseEventGroup>.Invoker invoker;

    private static class _BtEnablerBaseEventGroup extends BtEnablerBaseEventGroup {
        @NonNull
        public BaseEventGroup<BtEnablerBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BtEnablerBaseEventGroup getEventGroup() {
        return eventGroup;
    }

    @NonNull
    private final ActivityResultRegister register;

    protected BtEnabler(AppCompatActivity activity) {
        this.invoker = eventGroup.getInvoker();
        this.register = new ActivityResultRegister.Builder()
                .action(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                .buildOn(activity);
    }

    public void fire() {
        register.getEvents()
                .on(g->g.result, e -> {
                    if (e.resultCode == Activity.RESULT_OK) {
                        invoker.invoke(BtEnablerBaseEventGroup::getAgree, null);
                    } else {
                        invoker.invoke(BtEnablerBaseEventGroup::getDisagree, null);
                    }
                });
        register.fire();
    }
}
