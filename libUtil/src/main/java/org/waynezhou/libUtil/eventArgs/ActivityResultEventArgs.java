package org.waynezhou.libUtil.eventArgs;

import android.content.Intent;

@Deprecated
public class ActivityResultEventArgs {
    public final int resultCode;
    public final Intent intent;

    public ActivityResultEventArgs(int resultCode, Intent intent) {
        this.resultCode = resultCode;
        this.intent = intent;
    }
}
