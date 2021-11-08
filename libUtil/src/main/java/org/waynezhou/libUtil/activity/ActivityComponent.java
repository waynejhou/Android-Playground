package org.waynezhou.libUtil.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ActivityComponent<TActivity extends ComponentizedActivity<TActivity>> {
    private boolean inited = false;
    
    public boolean isInited() {
        return inited;
    }
    
    protected TActivity host;
    
    protected void onInit(){}
    
    void init(TActivity activity){
        host = activity;
        host.getEvents().on(g->g.create, this::onHostCreate);
        onInit();
        inited = true;
    }
    
    protected void onRelease(){}
    
    void release(){
        onRelease();
        inited = false;
    }
    
    protected abstract void onHostCreate(@Nullable Bundle savedInstanceState);
}
