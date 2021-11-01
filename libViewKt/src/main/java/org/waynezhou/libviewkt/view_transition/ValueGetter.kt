package org.waynezhou.libviewkt.view_transition

import android.view.View

abstract class ValueGetter<TSource:Any> constructor(
    private val getter :(TSource)->Float
) {
    @Suppress("UNCHECKED_CAST")
    internal fun getFrom(source: Any): Float{
        return getter(source as TSource)
    }

    class FromView constructor(
        private val getter: (View) -> Float,
    ) : ValueGetter<View>(getter)

    class FromHolder<TValueHolder : Any>(
        private val getter: (TValueHolder) -> Float
    ) :ValueGetter<TValueHolder>(getter)

}