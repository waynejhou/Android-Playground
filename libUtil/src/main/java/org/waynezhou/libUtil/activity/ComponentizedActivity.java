package org.waynezhou.libUtil.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.event.EventHolder;

public abstract class ComponentizedActivity<TActivity extends ComponentizedActivity<TActivity>> extends AppCompatActivity {
    public static class EventGroup extends BaseEventGroup<EventGroup> {
        @NonNull
        public final EventHolder<Bundle> create = new EventHolder<>();
        @NonNull
        public final EventHolder<Void> start = new EventHolder<>();
        @NonNull
        public final EventHolder<Void> resume = new EventHolder<>();
        @NonNull
        public final EventHolder<Void> pause = new EventHolder<>();
        @NonNull
        public final EventHolder<Void> stop = new EventHolder<>();
        @NonNull
        public final EventHolder<Void> destroy = new EventHolder<>();
        @NonNull
        public final EventHolder<Configuration> configurationChanged = new EventHolder<>();
        @NonNull
        public final EventHolder<KeyDownEventArgs> keyDown = new EventHolder<>();
        @NonNull
        public BaseEventGroup<EventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }
    
    private final EventGroup eventGroup = new EventGroup();
    
    public BaseEventGroup<EventGroup> getEvents() {
        return eventGroup;
    }
    
    private final BaseEventGroup<EventGroup>.Invoker invoker;
    
    public ComponentizedActivity() {
        invoker = eventGroup.getInvoker();
    }
    
    protected abstract ActivityComponent<TActivity>[] getComponents();
    
    @SuppressWarnings("unchecked")
    @CallSuper
    protected void onInitComponents(@Nullable Bundle savedInstanceState){
        for(ActivityComponent<TActivity> component : getComponents()){
            component.init((TActivity) this);
        }
    }
    
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
        for(ActivityComponent<TActivity> component : getComponents()){
            component.release();
        }
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

