package org.waynezhou.libviewkt.view_transition

import android.animation.*
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

class ViewTransition<TValueHolder : Any> constructor(
    private val valHolder: TValueHolder,
    private val viewSteps: List<ViewStep<TValueHolder>> = listOf(),
    private val conditionalViewSteps: List<ConditionalViewStep<TValueHolder>> = listOf(),
){
    fun createAnimatorSet(args: ViewAnimatorArgs): AnimatorSet{
        val set = AnimatorSet()
        val animators = mutableListOf<Animator>()

        viewSteps.forEach{animators.add(it.createAnimator(valHolder, args))}
        conditionalViewSteps.filter { it.condition() }
            .forEach { animators.add(it.viewStep.createAnimator(valHolder, args)) }
        set.playTogether(animators)
        return set
    }
    fun runWithoutAnimation(withRequestLayout: Boolean) {
        viewSteps.forEach {
            it.createExecutor(valHolder).execute(withRequestLayout)
        }
        conditionalViewSteps.filter { it.condition() }
            .forEach {
                it.viewStep.createExecutor(valHolder).execute(withRequestLayout)
            }
    }

}

class ViewStep<TValueHolder : Any> constructor(
    private val viewGetter: () -> View,
    private val propSteps: List<ViewPropStep<TValueHolder>> = listOf(),
    private val propFixes: List<ViewPropFix<TValueHolder>> = listOf(),
    private val preAction:(View)->Unit = {}
) {

    fun createAnimator(holder: TValueHolder, args: ViewAnimatorArgs): Animator {

        val holders = propSteps.map { it.createHolder(viewGetter, holder) }

        val holderArr = holders.toTypedArray()

        val ret = ValueAnimator()
        ret.duration = args.duration
        ret.interpolator = args.interpolator
        ret.setValues(*holderArr)
        ret.addUpdateListener { animator: ValueAnimator ->
            val view: View = viewGetter()
            holders.forEach {
                val bridge =
                    LayoutTransitionPropertyBridge.bridges[it.propertyName]
                        ?: throw NullPointerException()
                bridge.set(view, animator.getAnimatedValue(it.propertyName) as Float)
            }

            propFixes.forEach {
                val bridge: LayoutTransitionPropertyBridge =
                    LayoutTransitionPropertyBridge.bridges[it.propName]
                        ?: throw NullPointerException()
                val getter: ValueGetter = it.getter
                val out = ValueGetter.Out(0f);
                if (getter.tryGet(viewGetter(), out)) {
                    bridge.set(view, out.value)
                }
                if (getter.tryGet(holder, out)) {
                    bridge.set(view, out.value)
                }
            }

            view.requestLayout()
        }
        ret.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                preAction(viewGetter())
            }
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return ret
    }

    fun createExecutor(holder: TValueHolder): ViewStepExecutor {
        val finalSteps = propSteps.map {
            it.createFinalStep(viewGetter, holder)
        }

        return ViewStepExecutor { withRequestLayout: Boolean ->
            val view = viewGetter()
            preAction(view)

            finalSteps.forEach {
                val bridge = LayoutTransitionPropertyBridge.bridges[it.propName]
                    ?:throw NullPointerException()
                bridge.set(view, it.value)
            }

            propFixes.forEach {
                val bridge = LayoutTransitionPropertyBridge.bridges[it.propName]
                    ?:throw NullPointerException()
                val getter = it.getter
                val out = ValueGetter.Out(0f)
                if(getter.tryGet(viewGetter(),out )){
                    bridge.set(view, out.value)
                }
                if(getter.tryGet(viewGetter(),out )){
                    bridge.set(view, out.value)
                }
            }
            if (withRequestLayout) view.requestLayout()
        }
    }

    fun transpose(): ViewStep<TValueHolder> {
        val transposedPropSteps = propSteps.map{it.transpose()}

        val transposedPropFixes = propFixes.map{it.transpose()}

        return ViewStep(
            viewGetter,
            transposedPropSteps,
            transposedPropFixes,
            this.preAction
        )
    }

    fun interface ViewStepExecutor {
        fun execute(withRequestLayout: Boolean)
    }
}

class ConditionalViewStep<TValueHolder : Any> constructor(
    internal val condition: () -> Boolean,
    internal val viewStep: ViewStep<TValueHolder>
)

class ViewPropStep<TValueHolder : Any> constructor(
    private val propName: String,
    private val valueGetters: List<ValueGetter> = listOf(),
) {
    internal fun createHolder(viewGetter: () -> View, holder: TValueHolder): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(propName, *(valueGetters.map {
            val getter: ValueGetter = it
            val out = ValueGetter.Out(0f);

            if (getter.tryGet(viewGetter(), out)) {
                out.value;
            }
            if (getter.tryGet(holder, out)) {
                out.value;
            }
            throw NotImplementedError()
        }.toFloatArray()))
    }

    internal fun createFinalStep(viewGetter: () -> View, holder: TValueHolder): ViewPropFinalStep {
        val getter: ValueGetter = valueGetters.last()
        val out = ValueGetter.Out(0f);

        if (getter.tryGet(viewGetter(), out)) {
            return ViewPropFinalStep(propName, out.value)
        }
        if (getter.tryGet(holder, out)) {
            return ViewPropFinalStep(propName, out.value)
        }
        throw NotImplementedError()
    }

    fun transpose(): ViewPropStep<TValueHolder> {
        LayoutTransitionPropertyBridge.transposeMap[propName]?.let {
            return ViewPropStep(it, valueGetters);
        }
        return this;
    }

    internal class ViewPropFinalStep(val propName: String, val value: Float)
}

class ViewPropFix<TValueHolder : Any> constructor(
    internal val propName: String,
    internal val getter: ValueGetter,
) {
    fun transpose(): ViewPropFix<TValueHolder> {
        LayoutTransitionPropertyBridge.transposeMap[propName]?.let {
            return ViewPropFix(it, getter);
        }
        return this;
    }
}


abstract class ValueGetter {
    internal abstract fun tryGet(source: Any, outVal: Out<Float>): Boolean
    class FromView constructor(
        private val getter: (View) -> Float,
    ) : ValueGetter() {
        override fun tryGet(source: Any, outVal: Out<Float>): Boolean {
            if (source is View) {
                outVal.value = getter(source);
                return true;
            }
            return false;
        }
    }

    class FromHolder<TValueHolder : Any>(
        private val getter: (TValueHolder) -> Float
    ) : ValueGetter() {
        override fun tryGet(source: Any, outVal: Out<Float>): Boolean {
            val holder: TValueHolder? = source as? TValueHolder;
            if (holder != null) {
                outVal.value = getter(holder);
                return true
            }
            return false
        }

    }

    class Out<T> constructor(var value: T)
}

data class ViewAnimatorArgs constructor(
    val duration: Long = 500,
    val interpolator: TimeInterpolator,
)

abstract class LayoutTransitionPropertyBridge {
    abstract fun get(view: View): Float

    abstract fun set(view: View, value: Float)


    internal class Width : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.width.toFloat()
        }

        override fun set(view: View, value: Float) {
            view.layoutParams.width = value.roundToInt()
        }
    }

    internal class Height : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.height.toFloat()
        }

        override fun set(view: View, value: Float) {
            view.layoutParams.height = value.roundToInt()
        }
    }

    internal class Left : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.left.toFloat()
        }

        override fun set(view: View, value: Float) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = value.roundToInt()
        }
    }

    internal class Right : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.right.toFloat()
        }

        override fun set(view: View, value: Float) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = value.roundToInt()
        }
    }

    internal class Top : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.top.toFloat()
        }

        override fun set(view: View, value: Float) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin = value.roundToInt()
        }
    }

    internal class Bottom : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.bottom.toFloat()
        }

        override fun set(view: View, value: Float) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value.roundToInt()
        }
    }

    internal class Gravity : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            if (view.layoutParams is LinearLayout.LayoutParams) {
                return (view.layoutParams as LinearLayout.LayoutParams).gravity.toFloat()
            } else if (view.layoutParams is FrameLayout.LayoutParams) {
                return (view.layoutParams as FrameLayout.LayoutParams).gravity.toFloat()
            }
            assert(false)
            return 0f
        }

        override fun set(view: View, value: Float) {
            if (view.layoutParams is LinearLayout.LayoutParams) {
                (view.layoutParams as LinearLayout.LayoutParams).gravity = value.toInt()
            } else if (view.layoutParams is FrameLayout.LayoutParams) {
                (view.layoutParams as FrameLayout.LayoutParams).gravity = value.toInt()
            }
            assert(false)
        }
    }

    internal class ScaleX : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.scaleX
        }

        override fun set(view: View, value: Float) {
            view.scaleX = value
        }
    }

    internal class ScaleY : LayoutTransitionPropertyBridge() {
        override fun get(view: View): Float {
            return view.scaleY
        }

        override fun set(view: View, value: Float) {
            view.scaleY = value
        }
    }


    companion object {
        val PROP_WID = "width"
        val width: LayoutTransitionPropertyBridge = Width()
        val PROP_HEI = "height"
        val height: LayoutTransitionPropertyBridge = Height()
        val PROP_LFT = "left"
        val left: LayoutTransitionPropertyBridge = Left()
        val PROP_RGT = "right"
        val right: LayoutTransitionPropertyBridge = Right()
        val PROP_TOP = "top"
        val top: LayoutTransitionPropertyBridge = Top()
        val PROP_BTM = "bottom"
        val bottom: LayoutTransitionPropertyBridge = Bottom()
        val PROP_GVT = "gravity"
        val gravity: LayoutTransitionPropertyBridge = Gravity()
        val PROP_SCX = "scaleX"
        val scaleX: LayoutTransitionPropertyBridge = ScaleX()
        val PROP_SCY = "scaleY"
        val scaleY: LayoutTransitionPropertyBridge = ScaleY()
        val bridges: Map<String, LayoutTransitionPropertyBridge> = mapOf(
            Pair(PROP_WID, width),
            Pair(PROP_HEI, height),
            Pair(PROP_LFT, left),
            Pair(PROP_RGT, right),
            Pair(PROP_TOP, top),
            Pair(PROP_BTM, bottom),
            Pair(PROP_GVT, gravity),
            Pair(PROP_SCY, scaleY),
        )
        val transposeMap: Map<String, String> = mapOf(
            Pair(PROP_WID, PROP_HEI),
            Pair(PROP_HEI, PROP_WID),
            Pair(PROP_LFT, PROP_TOP),
            Pair(PROP_RGT, PROP_BTM),
            Pair(PROP_TOP, PROP_LFT),
            Pair(PROP_BTM, PROP_RGT),
            Pair(PROP_GVT, PROP_GVT),
            Pair(PROP_SCX, PROP_SCY),
            Pair(PROP_SCY, PROP_SCX),
        )
    }
}