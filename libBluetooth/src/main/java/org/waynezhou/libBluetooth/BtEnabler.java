package org.waynezhou.libBluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.ActivityResultRegister;
import org.waynezhou.libUtil.event.EventGroup;

public final class BtEnabler {
    private final _BtEnablerEventGroup eventGroup = new _BtEnablerEventGroup();
    private final EventGroup<BtEnablerEventGroup>.Invoker invoker;

    private static class _BtEnablerEventGroup extends BtEnablerEventGroup {
        public EventGroup<BtEnablerEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public BtEnablerEventGroup getEventGroup() {
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
        register.getEventGroup()
                .on(g->g.result, e -> {
                    if (e.resultCode == Activity.RESULT_OK) {
                        invoker.invoke(BtEnablerEventGroup::getAgree, null);
                    } else {
                        invoker.invoke(BtEnablerEventGroup::getDisagree, null);
                    }
                });
        register.fire();
    }
}
