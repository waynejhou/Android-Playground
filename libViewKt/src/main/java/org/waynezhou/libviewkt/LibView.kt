package org.waynezhou.libviewkt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import org.waynezhou.libutilkt.reflection.ReflectionException
import java.lang.reflect.InvocationTargetException

object LibView {
    @Throws(ReflectionException::class)
    @Suppress("UNCHECKED_CAST")
    fun <TViewBinding : ViewBinding> inflate(
        layoutInflater: LayoutInflater,
        viewBindingType: Class<TViewBinding>,
        container: ViewGroup?,
        attachToParent: Boolean
    ): TViewBinding {
        return try {
            val inflateMethod = viewBindingType.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            inflateMethod.invoke(null, layoutInflater, container, attachToParent) as TViewBinding
        } catch (e: IllegalAccessException) {
            throw ReflectionException(e)
        } catch (e: InvocationTargetException) {
            throw ReflectionException(e)
        } catch (e: NoSuchMethodException) {
            throw ReflectionException(e)
        }
    }

    private val dataContextMap = mutableMapOf<Int, Any?>()

    var View.dataContext: Any?
        get() = dataContextMap[hashCode()]
        set(value) {
            dataContextMap[hashCode()] = value
        }
}