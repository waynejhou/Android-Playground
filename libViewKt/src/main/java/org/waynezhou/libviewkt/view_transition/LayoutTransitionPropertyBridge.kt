package org.waynezhou.libviewkt.view_transition

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlin.math.roundToInt

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

}


val PROP_WID = "width"
val propWidth: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Width()
val PROP_HEI = "height"
val propHeight: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Height()
val PROP_LFT = "left"
val propLeft: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Left()
val PROP_RGT = "right"
val propRight: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Right()
val PROP_TOP = "top"
val propTop: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Top()
val PROP_BTM = "bottom"
val propBottom: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Bottom()
val PROP_GVT = "gravity"
val propGravity: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.Gravity()
val PROP_SCX = "scaleX"
val propScaleX: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.ScaleX()
val PROP_SCY = "scaleY"
val propScaleY: LayoutTransitionPropertyBridge = LayoutTransitionPropertyBridge.ScaleY()
val propBridges: Map<String, LayoutTransitionPropertyBridge> = mapOf(
    Pair(PROP_WID, propWidth),
    Pair(PROP_HEI, propHeight),
    Pair(PROP_LFT, propLeft),
    Pair(PROP_RGT, propRight),
    Pair(PROP_TOP, propTop),
    Pair(PROP_BTM, propBottom),
    Pair(PROP_GVT, propGravity),
    Pair(PROP_SCY, propScaleY),
)
val propTransposeMap: Map<String, String> = mapOf(
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