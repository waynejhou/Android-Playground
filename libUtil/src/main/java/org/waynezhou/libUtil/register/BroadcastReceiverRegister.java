package org.waynezhou.libUtil.activity_register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public class BroadcastReceiverRegister {
    
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<BroadcastReceiveArgs> result = new EventHolder<>();
        
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
    
    
    public BroadcastReceiverRegister(
      @NonNull final AppCompatActivity activity,
      @NonNull IntentFilter intentFilter
    ) {
        invoker = eventGroup.getPrivateInvoker();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(
              Context context,
              Intent intent
            ) {
                invoker.invoke(g->g.result, new BroadcastReceiveArgs(context, intent));
            }
        };
        activity.registerReceiver(broadcastReceiver, intentFilter);
    }
    
}

