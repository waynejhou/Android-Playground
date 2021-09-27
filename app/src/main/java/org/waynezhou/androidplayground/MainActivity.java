package org.waynezhou.androidplayground;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.waynezhou.androidplayground.databinding.ActivityMainBinding;
import org.waynezhou.androidplayground.layout.LayoutDestination;
import org.waynezhou.androidplayground.layout.LayoutManager;
import org.waynezhou.androidplayground.layout.LayoutTemplate;
import org.waynezhou.libUtil.StandardKt;

import static org.waynezhou.androidplayground.layout.LayoutProperties.*;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LayoutManager<MainActivity> layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        layoutManager = new LayoutManager<>(this);

        layoutManager.openTemplate(landscape)
                .setPostAction(() -> {

                }).execute();

    }

    StandardKt.RunBlock<LayoutDestination.ViewDestinationBuilder<MainActivity>> layoutPortraitTop = it->{
        it.set("")x
    }
    LayoutTemplate<MainActivity> landscape = new LayoutTemplate<MainActivity>()
            .setDest(LayoutDestination.builder(MainActivity.class)
                    .beginConfig(binding.mainTopContainer)
                    .set(PROP_WIDTH, GET_VIEW_WIDTH(binding.mainTopContainer), $ -> binding.getRoot().getMeasuredWidth())
                    .set(PROP_HEIGHT, GET_VIEW_HEIGHT(binding.mainTopContainer), $ -> binding.getRoot().getMeasuredHeight())
                    .endConfig()
                    .beginConfig(binding.mainMiddleContainer)
                    .apply(it->{

                    })
                    .build());

    /**
     * property values: [property name, value getter...]
     * view destination: [property values ...]
     * layout destination: [view destination ...]
     * layout template: [layout destination, layout args]
     * layout Manager
     * layout executor [layout template, postaction]
     * execute
     */
}
