package org.waynezhou.libutilkt.event

abstract class BaseEventGroup<TEventGroup : BaseEventGroup<TEventGroup>> protected constructor() {

    @Suppress("UNCHECKED_CAST")
    fun <TEventArgs> on(
        selector: (TEventGroup) -> EventHolder<TEventArgs>,
        listener: EventListener<TEventArgs>
    ): TEventGroup {
        selector(this as TEventGroup).addOnListener(listener)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <TEventArgs> once(
        selector: (TEventGroup) -> EventHolder<TEventArgs>,
        listener: EventListener<TEventArgs>
    ): TEventGroup {
        selector(this as TEventGroup).addOnceListener(listener)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <TEventArgs> tokenOn(
        selector: (TEventGroup) -> EventHolder<TEventArgs>,
        listener: EventListener<TEventArgs>
    ): EventHolder<TEventArgs>.ListenerToken {
        return selector(this as TEventGroup).addOnListener(listener)
    }

    @Suppress("UNCHECKED_CAST")
    fun <TEventArgs> tokenOnce(
        selector: (TEventGroup) -> EventHolder<TEventArgs>,
        listener: EventListener<TEventArgs>
    ): EventHolder<TEventArgs>.ListenerToken {
        return selector(this as TEventGroup).addOnceListener(listener)
    }

    inner class Invoker internal constructor(
        private val group: TEventGroup
    ) {
        fun <TEventArgs> invoke(
            selector: (TEventGroup) -> EventHolder<TEventArgs>,
            eventArgs: TEventArgs,
            breakAction: () -> Boolean = { false }
        ) {
            selector(group).invoke(eventArgs, breakAction)
        }
    }

    private var invoker: BaseEventGroup<TEventGroup>.Invoker? = null

    @Suppress("UNCHECKED_CAST")
    protected fun getInvoker(): BaseEventGroup<TEventGroup>.Invoker {
        if (invoker == null) invoker = Invoker(this as TEventGroup)
        return invoker!!
    }

    @Suppress("UNCHECKED_CAST")
     fun <TEventArgs> remove(
        selector: (TEventGroup) -> EventHolder<TEventArgs>,
        token: EventHolder<TEventArgs>.ListenerToken
    ): TEventGroup {
        selector(this as TEventGroup).removeListener(token)
        return this
    }

    @Suppress("UNCHECKED_CAST")
     fun <TEventArgs> remove(
        selector: (TEventGroup) -> EventHolder<TEventArgs>,
        listener: EventListener<TEventArgs>
    ): TEventGroup {
        selector(this as TEventGroup).removeListener(listener)
        return this
    }

}

class EventHolder<TEventArgs> {
    private inner class Listener(
        val isOnce: Boolean,
        val listener: EventListener<TEventArgs>
    )

    private val listeners: MutableList<Listener> = mutableListOf()
    internal fun addOnceListener(listener: EventListener<TEventArgs>): ListenerToken {
        val l = Listener(true, listener)
        val token = ListenerToken(l.hashCode())
        listeners.add(l)
        return token
    }

    internal fun addOnListener(listener: EventListener<TEventArgs>): ListenerToken {
        val l = Listener(false, listener)
        val token = ListenerToken(l.hashCode())
        listeners.add(l)
        return token
    }

    internal fun removeListener(listener: EventListener<TEventArgs>) {
        listeners.removeIf { x: Listener -> x.listener === listener }
    }

    internal fun removeListener(token: ListenerToken) {
        listeners.removeIf { x: Listener -> x.hashCode() == token.hashcode }
    }

    internal fun invoke(e: TEventArgs, breakAction: () -> Boolean) {
        val removedIdx: MutableList<Int> = mutableListOf()
        for ((idx, listener) in listeners.withIndex()) {
            listener.listener.invoke(e)
            if (listener.isOnce) {
                removedIdx.add(idx)
            }
            if (breakAction()) break
        }
        removedIdx.forEach { listeners.removeAt(it) }
    }

    inner class ListenerToken(val hashcode: Int)
}

fun interface EventListener<TEventArgs> {
    operator fun invoke(e: TEventArgs)
}