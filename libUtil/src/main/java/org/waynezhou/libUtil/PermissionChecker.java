package org.waynezhou.libUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.eventGroup.PermissionCheckerEventGroup;

import java.util.Map;

public class PermissionChecker {

    private final _PermissionCheckerEventGroup eventGroup = new _PermissionCheckerEventGroup();

    private final EventGroup<PermissionCheckerEventGroup>.Invoker invoker;

    private static class _PermissionCheckerEventGroup extends PermissionCheckerEventGroup {
        public EventGroup<PermissionCheckerEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public PermissionCheckerEventGroup getEventGroup() {
        return eventGroup;
    }

    private final ComponentActivity activity;
    private final ActivityResultLauncher<String[]> grantPermissionLauncher;
    private final String[] permissions;
    private final boolean tryGrantPermissionOnDenied;

    public PermissionChecker(final ComponentActivity activity, final boolean tryGrantPermissionOnDenied, final String... permissions) {
        this.invoker = eventGroup.getInvoker();
        this.activity = activity;
        this.permissions = permissions;
        this.tryGrantPermissionOnDenied = tryGrantPermissionOnDenied;
        this.grantPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                (map) -> {
                    //String result = new Linq<>(map.entrySet()).select(e -> e.getKey() + ": " + (e.getValue() ? "True" : "False")).join();
                    //LogHelper.d("RequestMultiplePermissions callback: " + result);
                    if (new Linq<>(map.values()).all(x -> x)) {
                        invoker.invoke(g -> g.permissionGranted,
                                new Linq<>(map.keySet()).list());
                        //this.eventPack.getPermissionGranted().invoke(new Linq<>(map.keySet()).list());
                    } else {
                        invoker.invoke(g -> g.permissionDenied,
                                new Linq<>(map.keySet())
                                        .where(x -> !(map.get(x)))
                                        .list());
                    }
                }
        );
    }

    //@Override
    public void fire() {
        Map<String, Boolean> result = new Linq<>(permissions)
                .map(x -> x, x -> ContextCompat.checkSelfPermission(activity, x) == PackageManager.PERMISSION_GRANTED);
        if (new Linq<>(result.values()).all(x -> x)) {
            invoker.invoke(g -> g.permissionGranted, new Linq<>(permissions).list());
        } else {
            if (tryGrantPermissionOnDenied) {
                grantPermissionLauncher.launch(permissions);
            } else {
                invoker.invoke(g -> g.permissionDenied,
                        new Linq<>(result.keySet())
                                .where(x -> !(result.get(x)))
                                .list());

            }
        }
    }
}
