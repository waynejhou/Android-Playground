package org.waynezhou.androidplayground.layout;

import android.view.View;

public interface ViewGetter<T> {
    View get(T it);
}
