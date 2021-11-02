package org.waynezhou.androidplayground.main

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.animation.addListener
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.libutilkt.EnumClass
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libviewkt.ActivityComponent
import org.waynezhou.libviewkt.view_transition.*


val contentViewSet = Layout.ChangedReason("ContentView Set")

internal interface IActivityLayout {
    val layouts: Layouts
    val binding: ActivityMainBinding
    fun applyCurrentLayout(
        args: ViewAnimatorArgs = ViewAnimatorArgs(),
        preAction: (Animator) -> Unit = { _ -> },
        postAction: (Animator) -> Unit = { _ -> }
    )

    var currentLayout: ViewTransition<Layouts>
}

class Layout : ActivityComponent<Activity>(), IActivityLayout {
    override lateinit var layouts: Layouts

    override lateinit var binding: ActivityMainBinding
        private set

    override fun onHostCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(host.layoutInflater)
        layouts = Layouts(binding)
        binding.root.viewTreeObserver.run {
            addOnGlobalLayoutListener(this@Layout::onGlobalLayout)
        }
        setChangedReason(contentViewSet);
        host.setContentView(binding.root)
    }

    private var changedReason: ChangedReason? = null
    private fun setChangedReason(reason: ChangedReason?) {
        changedReason = reason
    }

    private fun onGlobalLayout() {
        try {
            changedReason?.let { cReason ->
                LogHelper.i(cReason)
                if (!layouts.isRootBoundChanged) return
                layouts.measureLayout()
                if (cReason == contentViewSet) {
                    currentLayout.runWithoutAnimation(true)
                }
            }
        } finally {
            setChangedReason(null)
        }
    }

    private lateinit var _currentLayout: ViewTransition<Layouts>

    override var currentLayout: ViewTransition<Layouts>
        get() = _currentLayout
        set(value) {
            _currentLayout = value
        }


    class ChangedReason constructor(statement: String) : EnumClass.Int(statement)


    override fun applyCurrentLayout(
        args: ViewAnimatorArgs,
        preAction: (Animator) -> Unit,
        postAction: (Animator) -> Unit
    ) {
        val set = currentLayout.createAnimatorSet(args)
        set.addListener(onStart = preAction, onEnd = postAction)
        set.start()
    }
}


typealias LayoutBuilder = ViewTransition.Builder<Layouts>
typealias ViewLayoutBuilder<TView> = ViewStep.Builder<Layouts, TView>
typealias ViewLayoutBuilderPreset<TView> = ViewLayoutBuilder<TView>.() -> ViewLayoutBuilder<TView>

class Layouts constructor(
    private val binding: ActivityMainBinding
) {
    val isRootBoundChanged
        get() = rootWid.toInt() != binding.root.width || rootHei.toInt() != binding.root.height

    fun measureLayout() {
        rootWid = binding.root.width.toFloat()
        rootHei = binding.root.height.toFloat()
    }

    private var activatedAudioControl = 0;
    private var rootWid = -1f
    private var rootHei = -1f

    private fun <TView : View> ViewLayoutBuilder<TView>.floatToTop(innerPreset: ViewLayoutBuilderPreset<TView>): ViewLayoutBuilder<TView> =
        run {
            preAction { z = 0f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 1f }
        }

    private fun <TView : View> ViewLayoutBuilder<TView>.sinkToBottom(innerPreset: ViewLayoutBuilderPreset<TView>): ViewLayoutBuilder<TView> =
        run {
            preAction { z = 1f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 0f; visibility = GONE }
        }

    private fun <TView : View> ViewLayoutBuilder<TView>.stayAtBottom(innerPreset: ViewLayoutBuilderPreset<TView>): ViewLayoutBuilder<TView> =
        run {
            preAction { z = 0f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 0f; visibility = GONE }
        }

    private fun <TView : View> ViewLayoutBuilder<TView>.stayAtTop(innerPreset: ViewLayoutBuilderPreset<TView>): ViewLayoutBuilder<TView> =
        run {
            preAction { z = 1f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 1f; visibility = VISIBLE }
        }

    private fun <TView : View> ViewLayoutBuilder<TView>.toFullScreen(): ViewLayoutBuilder<TView> =
        run {
            let(PROP_WID).startFromCurrent().toHolder { rootWid }.end()
            let(PROP_HEI).startFromCurrent().toHolder { rootHei }.end()
            let(PROP_LFT).startFromCurrent().toHolder { 0f }.end()
            let(PROP_TOP).startFromCurrent().toHolder { 0f }.end()
        }

    private fun <TView : View> ViewLayoutBuilder<TView>.toHideBottom(): ViewLayoutBuilder<TView> =
        run {
            let(PROP_WID).startFromCurrent().toHolder { rootWid }.end()
            let(PROP_HEI).startFromCurrent().toHolder { rootHei }.end()
            let(PROP_LFT).startFromCurrent().toHolder { 0f }.end()
            let(PROP_TOP).startFromCurrent().toHolder { rootHei }.end()
        }


    internal val audioList = LayoutBuilder(this).run {
        beginAddStep { binding.mainAudioList }
            .floatToTop { toFullScreen() }
            .endAddStep()
        beginAddStep { binding.mainAudioControl }
            .sinkToBottom { toHideBottom() }
            .endAddStep()
    }.build();

    internal val audioControl = LayoutBuilder(this).run {
        LogHelper.d(activatedAudioControl)
        beginAddStep { binding.mainAudioList }
            .stayAtBottom { toFullScreen() }
            .endAddStep()
        beginAddStep { binding.mainAudioControl }
            .stayAtTop { toFullScreen() }
            .endAddStep()
    }.build();
}