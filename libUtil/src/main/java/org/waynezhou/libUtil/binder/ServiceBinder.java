package org.waynezhou.libUtil.binder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;
import org.waynezhou.libUtil.eventArgs.ServiceBinderConnectedEventArgs;
import org.waynezhou.libUtil.log.LogHelper;

@Deprecated
public class ServiceBinder<TService extends Service, TServiceLocalBinder extends IBinder>{
    
    public static class EventGroup<TServiceLocalBinder> extends BaseEventGroup<EventGroup<TServiceLocalBinder>> {
        @NonNull
        public final EventHolder<ServiceBinderConnectedEventArgs<TServiceLocalBinder>> connected = new EventHolder<>();
        @NonNull
        public final EventHolder<ComponentName> disconnected = new EventHolder<>();
        @NonNull
        public final EventHolder<ComponentName> bindingDied = new EventHolder<>();
        @NonNull
        public final EventHolder<ComponentName> nullBinding = new EventHolder<>();
        @NonNull
        public final EventHolder<Object> connectionFailed = new EventHolder<>();
        
        @NonNull
        Invoker getPrivateInvoker() {
            return getInvoker();
        }
    }
    
    @NonNull
    private final EventGroup<TServiceLocalBinder> eventGroup = new EventGroup<>();
    
    @NonNull
    public BaseEventGroup<EventGroup<TServiceLocalBinder>> getEvents() {
        return eventGroup;
    }
    
    @NonNull
    private final BaseEventGroup<EventGroup<TServiceLocalBinder>>.Invoker invoker;
    
    
    public final ContextWrapper activity;
    public final ServiceConnection connection;
    public final Class<TService> serviceClass;
    
    public ServiceBinder(
      final ContextWrapper activity,
      Class<TService> serviceClass
    ) {
        this.invoker = eventGroup.getPrivateInvoker();
        this.activity = activity;
        this.serviceClass = serviceClass;
        this.connection = new ServiceConnection() {
            @SuppressWarnings("unchecked")
            @Override
            public void onServiceConnected(
              ComponentName name,
              IBinder service
            ) {
                LogHelper.d("service connected");
                ServiceBinder.this.invoker.invoke(g -> g.connected, new ServiceBinderConnectedEventArgs<>(name, (TServiceLocalBinder) service));
            }
            
            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogHelper.d("service disconnected");
                ServiceBinder.this.invoker.invoke(g -> g.disconnected, name);
            }
            
            @Override
            public void onBindingDied(ComponentName name) {
                LogHelper.d("service binding died");
                ServiceBinder.this.invoker.invoke(g -> g.bindingDied, name);
            }
            
            @Override
            public void onNullBinding(ComponentName name) {
                LogHelper.d("service null binding");
                ServiceBinder.this.invoker.invoke(g -> g.nullBinding, name);
            }
        };
    }
    
    public void bind() {
        boolean success = activity.bindService(new Intent(activity, serviceClass), connection, Context.BIND_AUTO_CREATE);
        if (!success) {
            invoker.invoke(g -> g.connectionFailed, null);
        }
    }
    
    public void unBind() {
        if (connection != null) {
            activity.unbindService(connection);
        }
    }
}
