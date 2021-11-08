package org.waynezhou.libUtil.checker;

import android.content.pm.PackageManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.linq.Linq;

import java.util.List;
import java.util.Map;

public class PermissionChecker {
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<List<String>> permissionGranted = new EventHolder<>();
        @NonNull
        public final EventHolder<List<String>> permissionDenied = new EventHolder<>();
        
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
    private final ComponentActivity activity;
    @NonNull
    private final ActivityResultLauncher<String[]> grantPermissionLauncher;
    @NonNull
    private final String[] permissions;
    private final boolean tryGrantPermissionOnDenied;
    
    @SuppressWarnings("ConstantConditions")
    public PermissionChecker(
      @NonNull final ComponentActivity activity,
      final boolean tryGrantPermissionOnDenied,
      @NonNull final String... permissions
    ) {
        this.invoker = eventGroup.getPrivateInvoker();
        this.activity = activity;
        this.permissions = permissions;
        this.tryGrantPermissionOnDenied = tryGrantPermissionOnDenied;
        this.grantPermissionLauncher = activity.registerForActivityResult(
          new ActivityResultContracts.RequestMultiplePermissions(),
          (map) -> {
              if (new Linq<>(map.values()).all(x -> x)) {
                  invoker.invoke(g -> g.permissionGranted,
                    new Linq<>(map.keySet()).list());
              } else {
                  invoker.invoke(g -> g.permissionDenied,
                    new Linq<>(map.keySet())
                      .where(x -> !(map.get(x)))
                      .list());
              }
          }
        );
    }
    
    @SuppressWarnings("ConstantConditions")
    public void fire() {
        @NonNull final Map<String, Boolean> result = new Linq<>(permissions)
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
