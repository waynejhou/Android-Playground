package org.waynezhou.libviewkt.view_transition

import android.animation.PropertyValuesHolder
import android.view.View
import java.util.*

class ViewPropStep<TValueHolder : Any> private constructor(
    private val propName: String,
    private val valueGetters: List<ValueGetter<*>>,
) {
    class Builder<TValueHolder : Any, TView: View> private constructor(
        private val host: ViewStep.Builder<TValueHolder, TView>,
        private val propName: String,
        private val valueGetters: MutableList<ValueGetter<*>>,
    ) {
        constructor(host: ViewStep.Builder<TValueHolder, TView>, propName: String) : this(
            host,
            propName,
            mutableListOf()
        )

        constructor(host: ViewStep.Builder<TValueHolder, TView>, other: ViewPropStep<TValueHolder>) : this(
            host,
            other.propName,
            other.valueGetters.toMutableList()
        )

        fun set(vararg getters: ValueGetter<*>): Builder<TValueHolder, TView> {
            valueGetters.clear()
            return to(*getters)
        }

        fun to(vararg getters: ValueGetter<*>): Builder<TValueHolder, TView> {
            Collections.addAll(valueGetters, *getters)
            return this
        }

        fun toHolder(getter: TValueHolder.() -> Float): Builder<TValueHolder, TView> {
            valueGetters.add(ValueGetter.FromHolder(getter))
            return this
        }

        fun toView(getter: View.() -> Float): Builder<TValueHolder, TView> {
            valueGetters.add(ValueGetter.FromView(getter))
            return this
        }

        fun startFromCurrent(): Builder<TValueHolder, TView> {
            valueGetters.clear()
            return toCurrent()
        }

        fun toCurrent(): Builder<TValueHolder, TView> {
            propBridges[propName]?.let { bridge ->
                valueGetters.add(ValueGetter.FromView { view -> bridge.get(view) })
            }
            return this
        }

        fun end(): ViewStep.Builder<TValueHolder, TView> {
            require(valueGetters.size > 0)
            for (i in host.propSteps.indices.reversed()) {
                if (host.propSteps[i].propName == propName) {
                    host.propSteps.removeAt(i)
                    break
                }
            }
            host.propSteps.add(ViewPropStep(propName, valueGetters))
            return host
        }
    }

    internal fun createHolder(viewGetter: () -> View, holder: TValueHolder): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(propName, *(valueGetters.map { getter -> when(getter){
            is ValueGetter.FromView -> getter.getFrom(viewGetter())
            is ValueGetter.FromHolder<*> -> getter.getFrom(holder)
            else -> throw NotImplementedError()
        }}.toFloatArray()))
    }

    internal fun createFinalStep(viewGetter: () -> View, holder: TValueHolder): ViewPropFinalStep {
        valueGetters.last().run {
            return ViewPropFinalStep(propName, when(this){
                is ValueGetter.FromView -> getFrom(viewGetter())
                is ValueGetter.FromHolder<*> -> getFrom(holder)
                else -> throw NotImplementedError()
            })
        }
    }

    internal fun transpose(): ViewPropStep<TValueHolder> {
        propTransposeMap[propName]?.let {
            return ViewPropStep(it, valueGetters)
        }
        return this
    }

    internal class ViewPropFinalStep(val propName: String, val value: Float)
}

class ViewPropFix<TValueHolder : Any> private constructor(
    internal val propName: String,
    internal val getter: ValueGetter<*>,
) {
    class Builder<TValueHolder : Any, TView:View> private constructor(
        private val host: ViewStep.Builder<TValueHolder, TView>,
        private val propName: String,
        private var getter: ValueGetter<*>?
    ) {
        constructor(host: ViewStep.Builder<TValueHolder, TView>, propName: String) : this(
            host,
            propName,
            null
        )

        constructor(host: ViewStep.Builder<TValueHolder, TView>, other: ViewPropFix<TValueHolder>) : this(
            host,
            other.propName,
            other.getter
        )

        fun with(getter: ValueGetter<*>): Builder<TValueHolder, TView> {
            this.getter = getter
            return this
        }
        fun end(): ViewStep.Builder<TValueHolder, TView> {
            requireNotNull(getter)
            for (i in host.propFixes.indices.reversed()) {
                if (host.propFixes[i].propName == propName) {
                    host.propFixes.removeAt(i)
                    break
                }
            }
            host.propFixes.add(ViewPropFix(propName, getter!!))
            return host
        }
    }

    internal fun transpose(): ViewPropFix<TValueHolder> {
        propTransposeMap[propName]?.let {
            return ViewPropFix(it, getter)
        }
        return this
    }
}
