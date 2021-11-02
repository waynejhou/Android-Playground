package org.waynezhou.libutilkt.event

class EventHandler<TEventArgs> {
    private val holder = EventHolder<TEventArgs>()

    fun on(listener: EventListener<TEventArgs>): EventHolder<TEventArgs>.ListenerToken {
        return holder.addOnListener(listener)
    }

    fun once(listener: EventListener<TEventArgs>): EventHolder<TEventArgs>.ListenerToken {
        return holder.addOnceListener(listener)
    }

    fun removeListener(listener: EventListener<TEventArgs>) {
        holder.removeListener(listener)
    }

    fun removeListener(token: EventHolder<TEventArgs>.ListenerToken) {
        holder.removeListener(token)
    }

    fun invoke(e: TEventArgs, breakAction: ()->Boolean = {false}) {
        holder.invoke(e, breakAction)
    }
}
