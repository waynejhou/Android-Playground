package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class LocalBinderServiceConnection<TLocalBinder extends IBinder> implements ServiceConnection {

    private LocalBinderServiceConnectedListener<TLocalBinder> connectedListener = ($1,$2)->{};
    private ServiceDisconnectedListener disconnectedListener = ($)->{};
    private ServiceBindingDiedListener bindingDiedListener = ($)->{};
    private ServiceNullBindingListener nullBindingListener = ($)->{};

    public LocalBinderServiceConnection<TLocalBinder> setConnected(LocalBinderServiceConnectedListener<TLocalBinder> listener){
        connectedListener = listener;
        return this;
    }
    public LocalBinderServiceConnection<TLocalBinder> setDisconnected(ServiceDisconnectedListener listener){
        disconnectedListener = listener;
        return this;
    }
    public LocalBinderServiceConnection<TLocalBinder> setBindingDied(ServiceBindingDiedListener listener){
        bindingDiedListener = listener;
        return this;
    }
    public LocalBinderServiceConnection<TLocalBinder> setNullBinding(ServiceNullBindingListener listener){
        nullBindingListener = listener;
        return this;
    }
    @SuppressWarnings("unchecked")
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        connectedListener.invoke(name, (TLocalBinder)service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        disconnectedListener.invoke(name);
    }

    @Override
    public void onBindingDied(ComponentName name) {
        bindingDiedListener.invoke(name);
    }

    @Override
    public void onNullBinding(ComponentName name) {
        nullBindingListener.invoke(name);
    }
}
