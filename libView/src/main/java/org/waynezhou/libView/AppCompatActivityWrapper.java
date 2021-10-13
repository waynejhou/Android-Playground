package org.waynezhou.libView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.LogHelper;
import org.waynezhou.libUtil.event.EventHolder;

public abstract class AppCompatActivityWrapper extends AppCompatActivity {
    public static class EventGroup extends org.waynezhou.libUtil.event.EventGroup<EventGroup> {
        public final EventHolder<Bundle> create = new EventHolder<>();
        public final EventHolder<Void> start = new EventHolder<>();
        public final EventHolder<Void> resume = new EventHolder<>();
        public final EventHolder<Void> pause = new EventHolder<>();
        public final EventHolder<Void> stop = new EventHolder<>();
        public final EventHolder<Void> destroy = new EventHolder<>();
        public final EventHolder<Configuration> configurationChanged = new EventHolder<>();
        public final EventHolder<KeyDownEventArgs> keyDown = new EventHolder<>();
        public org.waynezhou.libUtil.event.EventGroup<EventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }
    
    private final EventGroup eventGroup = new EventGroup();
    
    public org.waynezhou.libUtil.event.EventGroup<EventGroup> getEventGroup() {
        return eventGroup;
    }
    
    private final org.waynezhou.libUtil.event.EventGroup<EventGroup>.Invoker invoker;
    
    public AppCompatActivityWrapper() {
        invoker = eventGroup.getInvoker();
    }
    
    public static class KeyDownEventArgs {
        public final int keyCode;
        public final KeyEvent keyEvent;
        
        public KeyDownEventArgs(int keyCode, KeyEvent keyEvent) {
            this.keyCode = keyCode;
            this.keyEvent = keyEvent;
        }
    }
    
    protected abstract void onInitComponents(@Nullable Bundle savedInstanceState);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onInitComponents(savedInstanceState);
        invoker.invoke(g -> g.create, savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        invoker.invoke(g -> g.destroy, null);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        invoker.invoke(g -> g.resume, null);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        invoker.invoke(g -> g.pause, null);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        invoker.invoke(g -> g.start, null);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        invoker.invoke(g -> g.stop, null);
    }
    
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        invoker.invoke(g -> g.configurationChanged, newConfig);
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        invoker.invoke(g -> g.keyDown, new KeyDownEventArgs(keyCode, event));
        return super.onKeyDown(keyCode, event);
    }
    
}
