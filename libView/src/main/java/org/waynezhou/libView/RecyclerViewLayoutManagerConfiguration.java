package org.waynezhou.libView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerViewLayoutManagerConfiguration {
    void config(@NonNull RecyclerView view, AppCompatActivity activity);
}
