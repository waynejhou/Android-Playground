package org.waynezhou.libView;

import android.view.View;

import androidx.annotation.NonNull;

@FunctionalInterface
public interface RecyclerItemClickListener {
    void invoke(@NonNull View view, @NonNull int position);
}
