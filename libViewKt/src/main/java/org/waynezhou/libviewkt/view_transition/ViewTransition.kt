package org.waynezhou.libviewkt.view_transition

import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.util.HashMap
import kotlin.math.roundToInt

class ViewTransition<TValueHolder : Any> constructor(
        private val valHolder: TValueHolder,
        private val viewSteps: List<ViewStep<TValueHolder>> = listOf(),
        private val conditionalViewSteps: List<ViewStep<TValueHolder>> = listOf(),
) {

}

class ViewStep<TValueHolder : Any> constructor(
        private val viewGetter: () -> View,
        private val propSteps: List<ViewPropStep<TValueHolder>> = listOf(),
        private val propFixes: List<ViewPropFix<TValueHolder>> = listOf(),
) {

}

class ConditionalViewStep<TValueHolder : Any> constructor(

) {

}


class ViewPropStep<TValueHolder : Any> constructor(
        private val propName: String,
        private val valueGetters: List<ValueGetter> = listOf(),
) {
    internal fun createHolder(viewGetter: () -> View, holder: TValueHolder): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(propName, *valueGetters.map {
            val getter: ValueGetter = it
            if (getter is ValueGetter.FromView) {
                getter.setView(viewGetter())
            }
            if (getter is ValueGetter.FromHolder<*>) {
                (getter as ValueGetter.FromHolder<TValueHolder>).setHolder(holder)
            }
            getter.get()}.toFloatArray())
    }

    internal fun createFinalStep(viewGetter: () -> View, holder: TValueHolder): ViewPropFinalStep {
        val getter: ValueGetter = valueGetters.last()
        if (getter is ValueGetter.FromView) {
            getter.setView(viewGetter())
        }
        if (getter is ValueGetter.FromHolder<*>) {
            (getter as ValueGetter.FromHolder<TValueHolder>).setHolder(holder)
        }

        return ViewPropFinalStep(propName, getter.get())
    }

    fun transpose(): ViewPropStep<TValueHolder> {
        LayoutTransitionPropertyBridge.transposeMap[propName]?.let{
            return ViewPropStep(it, valueGetters);
        }
        return this;
    }
    internal class ViewPropFinalStep(val propName: String, val value: Float)
}

class ViewPropFix<TValueHolder : Any> constructor(
        private val propName: String,
        private val getter: ValueGetter,
) {
    fun transpose(): ViewPropFix<TValueHolder> {
        LayoutTransitionPropertyBridge.transposeMap[propName]?.let{
            return ViewPropFix(it, getter);
        }
        return this;
    }
}


abstract class ValueGetter {
    abstract fun get(): Float
    class FromView constructor(
            private val getter: (View) -> Float,
    ) : ValueGetter() {
        private lateinit var view: View;
        internal fun setView(view: View) {
            this.view = view
        }

        override fun get(): Float {
            return getter(view)
        }
    }

    class FromHolder<TValueHolder : Any>(
            private val getter: (TValueHolder) -> Float
    ) : ValueGetter() {
        private lateinit var holder: TValueHolder;
        internal fun setHolder(holder: TValueHolder) {
            this.holder = holder
        }

        override fun get(): Float {
            return getter(holder)
        }

    }
}

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
        val bridge: Map<String, LayoutTransitionPropertyBridge> = mapOf(
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