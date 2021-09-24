package org.waynezhou.libUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverRegister extends BroadcastReceiver {
    public BroadcastReceiverRegister(){
        super();
    }
    private Listener broadcastReceiveListener = ($1,$2)->{};
    public BroadcastReceiverRegister setBroadReceive(Listener listener){
        broadcastReceiveListener = listener;
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        broadcastReceiveListener.invoke(context, intent);
    }

    @FunctionalInterface
    public interface Listener {
        void invoke(Context context, Intent intent);
    }

}

