package org.waynezhou.libView;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import static org.waynezhou.libUtil.UtilFunctions.setIfNotEqual;

public final class ViewFunctions {
    private ViewFunctions() {
    }

    public static <TView extends View> void setEnabledIfNot(TView view, boolean value) {
        setIfNotEqual(view, x -> x::isEnabled, value, x -> x::setEnabled);
    }

    public static <TView extends TextView> void setTextIfNot(TView view, String value) {
        setIfNotEqual(view, x -> x::getText, value, x -> x::setText);
    }

    public static <TView extends View> void onClick(TView view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }

    public static <TView extends TextView> void onIMEDone(TView view, View.OnClickListener listener) {
        view.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
                listener.onClick(v);
                return false;
        });
    }
}
