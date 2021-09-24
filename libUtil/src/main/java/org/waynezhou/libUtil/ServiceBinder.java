package org.waynezhou.libUtil;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.eventArgs.ServiceBinderConnectedEventArgs;
import org.waynezhou.libUtil.eventGroup.ServiceBinderEventGroup;

public class ServiceBinder<TService extends Service, TServiceLocalBinder extends IBinder> /* extends EventAction<ServiceBinder<TService, TServiceLocalBinder>, ServiceBinderEventGroup<TServiceLocalBinder>> */{

    private final _ServiceBinderEventGroup<TServiceLocalBinder> eventGroup = new _ServiceBinderEventGroup<>();
    private final EventGroup<ServiceBinderEventGroup<TServiceLocalBinder>>.Invoker invoker;

    private static class _ServiceBinderEventGroup<TServiceLocalBinder> extends ServiceBinderEventGroup<TServiceLocalBinder> {
        public EventGroup<ServiceBinderEventGroup<TServiceLocalBinder>>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public ServiceBinderEventGroup<TServiceLocalBinder> getEventGroup() {
        return eventGroup;
    }



    public final ContextWrapper activity;
    public final ServiceConnection connection;
    public final Class<TService> serviceClass;

    public ServiceBinder(final ContextWrapper activity, Class<TService> serviceClass) {
        // super(new ServiceBinderEventGroup<TServiceLocalBinder>());
        this.invoker = eventGroup.getInvoker();
        this.activity = activity;
        this.serviceClass = serviceClass;
        this.connection = new ServiceConnection() {
            @SuppressWarnings("unchecked")
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogHelper.d("service connected");
                ServiceBinder.this.invoker.invoke(g->g.connected, new ServiceBinderConnectedEventArgs<>(name, (TServiceLocalBinder) service));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogHelper.d("service disconnected");
                ServiceBinder.this.invoker.invoke(g->g.disconnected, name);
            }

            @Override
            public void onBindingDied(ComponentName name) {
                LogHelper.d("service binding died");
                ServiceBinder.this.invoker.invoke(g->g.bindingDied, name);
            }

            @Override
            public void onNullBinding(ComponentName name) {
                LogHelper.d("service null binding");
                ServiceBinder.this.invoker.invoke(g->g.nullBinding, name);
            }
        };
    }

    //@Override
    public void bind() {
        boolean success = activity.bindService(new Intent(activity, serviceClass), connection, Context.BIND_AUTO_CREATE);
        if(!success){
            invoker.invoke(g->g.connectionFailed, null);
        }
    }

    public void unBind(){
        if(connection!=null){
            activity.unbindService(connection);
        }
    }
}
