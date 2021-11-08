package org.waynezhou.libUtil.serviceConnection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

@Deprecated
public class MessengerServiceConnection implements ServiceConnection {

    private LocalBinderServiceConnectedListener<Messenger> connectedListener = ($1, $2) -> {
    };
    private ServiceDisconnectedListener disconnectedListener = ($) -> {
    };
    private ServiceBindingDiedListener bindingDiedListener = ($) -> {
    };
    private ServiceNullBindingListener nullBindingListener = ($) -> {
    };

    public MessengerServiceConnection setConnected(LocalBinderServiceConnectedListener<Messenger> listener) {
        connectedListener = listener;
        return this;
    }

    public MessengerServiceConnection setDisconnected(ServiceDisconnectedListener listener) {
        disconnectedListener = listener;
        return this;
    }

    public MessengerServiceConnection setBindingDied(ServiceBindingDiedListener listener) {
        bindingDiedListener = listener;
        return this;
    }

    public MessengerServiceConnection setNullBinding(ServiceNullBindingListener listener) {
        nullBindingListener = listener;
        return this;
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        connectedListener.invoke(name, new Messenger(service));
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
