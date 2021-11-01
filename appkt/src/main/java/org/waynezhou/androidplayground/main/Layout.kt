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
import org.waynezhou.libviewkt.view_transition.*

typealias ViewStepPreset<TView> = ViewStep.Builder<Layout, TView>.() -> ViewStep.Builder<Layout, TView>

val contentViewSet = Layout.ChangedReason("ContentView Set")

class Layout {
    private lateinit var host: Activity
    internal lateinit var binding: ActivityMainBinding
    internal fun init(activity: Activity) {
        host = activity
        host.events
            .on({ it.create }, this::onHostCreate)
    }

    private fun onHostCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(host.layoutInflater)
        binding.root.viewTreeObserver.run {
            addOnGlobalLayoutListener(this@Layout::onGlobalLayout)
        }
        setChangedReason(contentViewSet);
        host.setContentView(binding.root)
    }

    private var changedReason: ChangedReason? = null
    private var requireChangedInfo: StackTraceElement? = null;
    private fun setChangedReason(reason: ChangedReason?) {
        if (reason != null) {
            changedReason = reason
            requireChangedInfo = Thread.currentThread().stackTrace[3]
        } else {
            changedReason = null
            requireChangedInfo = null
        }
    }

    private var rootWid = -1f
    private var rootHei = -1f

    private fun onGlobalLayout() {
        try {
            changedReason?.let { cReason ->
                LogHelper.i(cReason)
                LogHelper.i("require changed at ${requireChangedInfo!!.methodName}(${requireChangedInfo!!.fileName}:${requireChangedInfo!!.lineNumber})")
                if (rootWid.toInt() == binding.root.width && rootHei.toInt() == binding.root.height) return
                rootWid = binding.root.width.toFloat()
                rootHei = binding.root.height.toFloat()
                if(cReason == contentViewSet){
                    current.runWithoutAnimation(true)
                }
            }
        } finally {
            setChangedReason(null)
        }
    }

    internal var activatedAudioControl = 0;


    private fun <TView : View> ViewStep.Builder<Layout, TView>.floatToTop(innerPreset: ViewStepPreset<TView>): ViewStep.Builder<Layout, TView> =
        run {
            preAction { z = 0f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 1f }
        }

    private fun <TView : View> ViewStep.Builder<Layout, TView>.sinkToBottom(innerPreset: ViewStepPreset<TView>): ViewStep.Builder<Layout, TView> =
        run {
            preAction { z = 1f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 0f; visibility = GONE }
        }

    private fun <TView : View> ViewStep.Builder<Layout, TView>.stayAtBottom(innerPreset: ViewStepPreset<TView>): ViewStep.Builder<Layout, TView> =
        run {
            preAction { z = 0f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 0f; visibility = GONE }
        }

    private fun <TView : View> ViewStep.Builder<Layout, TView>.stayAtTop(innerPreset: ViewStepPreset<TView>): ViewStep.Builder<Layout, TView> =
        run {
            preAction { z = 1f; visibility = VISIBLE }
            innerPreset()
            postAction { z = 1f; visibility = VISIBLE }
        }
    private fun <TView : View> ViewStep.Builder<Layout, TView>.toFullScreen(): ViewStep.Builder<Layout, TView> =
        run {
            let(PROP_WID).startFromCurrent().toHolder { rootWid }.end()
            let(PROP_HEI).startFromCurrent().toHolder { rootHei }.end()
            let(PROP_LFT).startFromCurrent().toHolder { 0f }.end()
            let(PROP_TOP).startFromCurrent().toHolder { 0f }.end()
        }

    private fun <TView : View> ViewStep.Builder<Layout, TView>.toHideBottom(): ViewStep.Builder<Layout, TView> =
        run {
            let(PROP_WID).startFromCurrent().toHolder { rootWid }.end()
            let(PROP_HEI).startFromCurrent().toHolder { rootHei }.end()
            let(PROP_LFT).startFromCurrent().toHolder { 0f }.end()
            let(PROP_TOP).startFromCurrent().toHolder { rootHei }.end()
        }



    internal val layoutAudioList = ViewTransition.Builder(this).run {
        beginAddStep { binding.mainAudioList }
            .floatToTop { toFullScreen() }
            .endAddStep()
        beginAddStep { binding.mainAudioControl1 }
            .sinkToBottom { toHideBottom() }
            .endAddStep()
        beginAddStep { binding.mainAudioControl2 }
            .sinkToBottom { toHideBottom() }
            .endAddStep()
    }.build();

    internal val layoutAudioControl = ViewTransition.Builder(this).run {
        beginAddStep { binding.mainAudioList }
            .stayAtBottom { toFullScreen() }
            .endAddStep()
        beginAddStep({ activatedAudioControl == 1 }, { binding.mainAudioControl1 })
            .stayAtTop { toFullScreen() }
            .endAddStep()
        beginAddStep({ activatedAudioControl != 1 }, { binding.mainAudioControl1 })
            .sinkToBottom { toHideBottom() }
            .endAddStep()
        beginAddStep({ activatedAudioControl == 2 }, { binding.mainAudioControl2 })
            .stayAtTop { toFullScreen() }
            .endAddStep()
        beginAddStep({ activatedAudioControl != 2 }, { binding.mainAudioControl2 })
            .sinkToBottom { toHideBottom() }
            .endAddStep()
    }.build();

    private var _current = layoutAudioList
    private var requestCurrentChangedInfo: StackTraceElement? = null
    internal var current: ViewTransition<Layout>
        get() = _current;
        set(value) {
            _current = value;
            requireChangedInfo = Thread.currentThread().stackTrace[3]
        }


    class ChangedReason constructor(statement: String) : EnumClass.Int(statement)


    fun refresh(
        args: ViewAnimatorArgs = ViewAnimatorArgs(),
        preAction: (Animator) -> Unit = {_->},
        postAction: (Animator) -> Unit = {_->}
    ) {
        val set = current.createAnimatorSet(args)
        set.addListener(onStart = preAction, onEnd = postAction)
        set.start()
    }
}