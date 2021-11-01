package org.waynezhou.libviewkt.view_transition

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View

/** Use getter to store layout of a view. */
class ViewStep<TValueHolder : Any> private constructor(
    private val viewGetter: () -> View,
    private val propSteps: List<ViewPropStep<TValueHolder>>,
    private val propFixes: List<ViewPropFix<TValueHolder>>,
    private val preAction: (View) -> Unit,
    private val postAction: (View) -> Unit
) {
    /** Builder of [ViewTransition]. */
    class Builder<TValueHolder : Any, TView : View> private constructor(
        private val host: ViewTransition.Builder<TValueHolder>,
        private val viewGetter: () -> TView,
        internal val propSteps: MutableList<ViewPropStep<TValueHolder>>,
        internal val propFixes: MutableList<ViewPropFix<TValueHolder>>,
        private val condition: (() -> Boolean)?,
        private var preAction: (TView) -> Unit,
        private var postAction: (TView) -> Unit
    ) {
        /** Empty instance builder Constructor. */
        constructor(host: ViewTransition.Builder<TValueHolder>, getter: () -> TView) : this(
            host,
            getter,
            mutableListOf(),
            mutableListOf(),
            null,
            {}, {})

        /** Empty instance with condition builder constructor. */
        constructor(
            host: ViewTransition.Builder<TValueHolder>,
            condition: () -> Boolean,
            getter: () -> TView
        ) : this(
            host,
            getter,
            mutableListOf(),
            mutableListOf(),
            condition,
            {}, {})

        /** Copy Instance Builder Constructor. */
        constructor(
            host: ViewTransition.Builder<TValueHolder>,
            other: ViewStep<TValueHolder>
        ) : this(
            host,
            other.viewGetter as () -> TView,
            other.propSteps.toMutableList(),
            other.propFixes.toMutableList(),
            null,
            { other.preAction },
            { other.postAction }
        )

        fun let(prop: String): ViewPropStep.Builder<TValueHolder, TView> {
            return ViewPropStep.Builder(this, prop)
        }

        fun preAction(action: TView.() -> Unit): Builder<TValueHolder, TView> {
            preAction = action
            return this
        }

        fun postAction(action: TView.() -> Unit): Builder<TValueHolder, TView> {
            postAction = action
            return this
        }

        fun endAddStep(): ViewTransition.Builder<TValueHolder> {
            if (condition == null)
                host.viewSteps.add(
                    ViewStep(
                        viewGetter,
                        propSteps,
                        propFixes,
                        preAction as (View) -> Unit,
                        postAction as (View) -> Unit
                    )
                )
            else
                host.conditionalViewSteps.add(
                    ConditionalViewStep(
                        condition,
                        ViewStep(
                            viewGetter,
                            propSteps,
                            propFixes,
                            preAction as (View) -> Unit,
                            postAction as (View) -> Unit
                        )
                    )
                )
            return host
        }
    }

    internal fun createAnimator(holder: TValueHolder, args: ViewAnimatorArgs): Animator {

        val holders = propSteps.map { it.createHolder(viewGetter, holder) }

        val holderArr = holders.toTypedArray()

        val ret = ValueAnimator()
        ret.duration = args.duration
        ret.interpolator = args.interpolator
        ret.setValues(*holderArr)
        ret.addUpdateListener { animator: ValueAnimator ->
            val view: View = viewGetter()
            holders.forEach {
                propBridges[it.propertyName]?.let { bridge ->
                    bridge.set(view, animator.getAnimatedValue(it.propertyName) as Float)
                    return@forEach
                }
                throw NullPointerException()
            }

            propFixes.forEach {
                propBridges[it.propName]?.let { bridge ->
                    when (val getter = it.getter) {
                        is ValueGetter.FromView -> bridge.set(view, getter.getFrom(view))
                        is ValueGetter.FromHolder<*> -> bridge.set(view, getter.getFrom(holder))
                    }
                    return@forEach
                }
                throw NullPointerException()
            }

            view.requestLayout()
        }
        ret.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                preAction(viewGetter())
            }

            override fun onAnimationEnd(animation: Animator) {
                postAction(viewGetter())
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return ret
    }

    internal fun createExecutor(holder: TValueHolder): ViewStepExecutor {
        val finalSteps = propSteps.map {
            it.createFinalStep(viewGetter, holder)
        }

        return ViewStepExecutor { withRequestLayout: Boolean ->
            val view = viewGetter()
            preAction(view)

            finalSteps.forEach {
                propBridges[it.propName]?.let { bridge ->
                    bridge.set(view, it.value)
                    return@forEach
                }
                throw NullPointerException()
            }

            propFixes.forEach {
                propBridges[it.propName]?.let { bridge ->
                    when (val getter = it.getter) {
                        is ValueGetter.FromView -> bridge.set(view, getter.getFrom(view))
                        is ValueGetter.FromHolder<*> -> bridge.set(view, getter.getFrom(holder))
                    }
                    return@forEach
                }
                throw NullPointerException()
            }
            if (withRequestLayout) view.requestLayout()
        }
    }

    internal fun transpose(): ViewStep<TValueHolder> {
        val transposedPropSteps = propSteps.map { it.transpose() }

        val transposedPropFixes = propFixes.map { it.transpose() }

        return ViewStep(
            viewGetter,
            transposedPropSteps,
            transposedPropFixes,
            this.preAction,
            this.postAction
        )
    }

    internal fun interface ViewStepExecutor {
        fun execute(withRequestLayout: Boolean)
    }
}


internal class ConditionalViewStep<TValueHolder : Any> internal constructor(
    internal val condition: () -> Boolean,
    internal val viewStep: ViewStep<TValueHolder>
)
