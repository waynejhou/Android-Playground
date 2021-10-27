package org.waynezhou.libView;


import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MotionEventExplain {
    private static final Map<Integer, String> actionExplain = new HashMap<Integer, String>(){{
        put(/* 0*/MotionEvent.ACTION_DOWN, "Down");
        put(/* 1*/MotionEvent.ACTION_UP, "Up");
        put(/* 2*/MotionEvent.ACTION_MOVE, "Move");
        put(/* 3*/MotionEvent.ACTION_CANCEL, "Cancel");
        put(/* 4*/MotionEvent.ACTION_OUTSIDE, "Outside");
        put(/* 5*/MotionEvent.ACTION_POINTER_DOWN, "Pointer Down");
        put(/* 6*/MotionEvent.ACTION_POINTER_UP, "Pointer Up");
        put(/* 7*/MotionEvent.ACTION_HOVER_MOVE, "Hover Move");
        put(/* 8*/MotionEvent.ACTION_SCROLL, "Scroll");
        put(/* 9*/MotionEvent.ACTION_HOVER_ENTER, "Hover Enter");
        put(/*10*/MotionEvent.ACTION_HOVER_EXIT, "Hover Exit");
        put(/*11*/MotionEvent.ACTION_BUTTON_PRESS, "Button Press");
        put(/*12*/MotionEvent.ACTION_BUTTON_RELEASE, "Button Release");
    }};
    public static String explainAction(int action){
        if(actionExplain.containsKey(action)){
            return String.format(Locale.TRADITIONAL_CHINESE,
              "Action Code: %d, Explain: %s"
              , action, actionExplain.get(action));
        }
        return String.format(Locale.TRADITIONAL_CHINESE,
          "Action Code: %d, Explain: Unknown Action"
          , action);
    }
}
