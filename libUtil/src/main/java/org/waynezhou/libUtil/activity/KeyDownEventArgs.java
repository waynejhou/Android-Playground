package org.waynezhou.libUtil.activity;

import android.view.KeyEvent;

public class KeyDownEventArgs {
    public final int keyCode;
    public final KeyEvent keyEvent;
    
    public KeyDownEventArgs(
      int keyCode,
      KeyEvent keyEvent
    ) {
        this.keyCode = keyCode;
        this.keyEvent = keyEvent;
    }
}
