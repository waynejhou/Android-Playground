package org.waynezhou.libUtil.register;

import android.content.Context;
import android.content.Intent;

public class BroadcastReceiveArgs {
    public final Context context;
    public final Intent intent;
    
    public BroadcastReceiveArgs(
      Context context,
      Intent intent
    ) {
        this.context = context;
        this.intent = intent;
    }
}
