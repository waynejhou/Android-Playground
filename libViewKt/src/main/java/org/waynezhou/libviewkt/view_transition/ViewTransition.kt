package org.waynezhou.libviewkt.view_transition

import android.animation.*
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import org.waynezhou.libviewkt.view_transition.ViewStep.ViewStepExecutor
import java.util.*

/** Use getter to store layout of views. */
class ViewTransition<TValueHolder : Any> private constructor(
    private val valHolder: TValueHolder,
    private val viewSteps: List<ViewStep<TValueHolder>>,
    private val conditionalViewSteps: List<ConditionalViewStep<TValueHolder>>,
) {
    /** Builder of [ViewTransition]. */
    class Builder<TValueHolder : Any> private constructor(
        private val valHolder: TValueHolder,
        internal val viewSteps: MutableList<ViewStep<TValueHolder>>,
        internal val conditionalViewSteps: MutableList<ConditionalViewStep<TValueHolder>>,
    ) {
        /** Empty Instance Builder Constructor. */
        constructor(valHolder: TValueHolder) : this(valHolder, mutableListOf(), mutableListOf())

        /** Copy Instance Builder Constructor. */
        constructor(other: ViewTransition<TValueHolder>) : this(
            other.valHolder,
            other.viewSteps.toMutableList(),
            other.conditionalViewSteps.toMutableList()
        )

        /** Add a transition step of a view. */
        fun <TView:View> beginAddStep(getter: () -> TView): ViewStep.Builder<TValueHolder, TView> {
            return ViewStep.Builder(this, getter)
        }

        /** Add a transition step of a view witch will be triggered in conditional. */
        fun <TView:View> beginAddStep(condition: () -> Boolean, getter: () -> TView) : ViewStep.Builder<TValueHolder, TView> {
            return ViewStep.Builder(this, condition, getter)
        }

        /** Build new [ViewTransition] instance. */
        fun build(): ViewTransition<TValueHolder> {
            return ViewTransition(valHolder, viewSteps.toList(), conditionalViewSteps.toList())
        }
    }

    /** Return new [AnimatorSet] instance that animate layout with captured value from getter. */
    fun createAnimatorSet(args: ViewAnimatorArgs): AnimatorSet {
        val set = AnimatorSet()
        val animators = mutableListOf<Animator>()

        viewSteps.forEach { animators.add(it.createAnimator(valHolder, args)) }
        conditionalViewSteps.filter { it.condition() }
            .forEach { animators.add(it.viewStep.createAnimator(valHolder, args)) }
        set.playTogether(animators)
        return set
    }

    /** Change layout immediately without animation. */
    fun runWithoutAnimation(withRequestLayout: Boolean) {
        viewSteps.forEach {
            it.createExecutor(valHolder).execute(withRequestLayout)
        }
        conditionalViewSteps.filter { it.condition() }
            .forEach {
                it.viewStep.createExecutor(valHolder).execute(withRequestLayout)
            }
    }

    fun transpose(): ViewTransition<TValueHolder>{
        return ViewTransition(
            valHolder,
            viewSteps.map{it.transpose()},
            conditionalViewSteps.map { ConditionalViewStep(it.condition, it.viewStep.transpose()) }
        )
    }


}

data class ViewAnimatorArgs constructor(
    val duration: Long = 500,
    val interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
)
