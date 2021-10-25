package org.waynezhou.libviewkt

import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import org.waynezhou.libutilkt.event.EventHolder
import java.util.*


abstract class AppCompatActivityWrapper : AppCompatActivity() {
    class EventGroup : org.waynezhou.libutilkt.event.EventGroup<EventGroup>() {
        val create = EventHolder<Bundle?>()
        val start = EventHolder<Unit>()
        val resume = EventHolder<Unit>()
        val pause = EventHolder<Unit>()
        val stop = EventHolder<Unit>()
        val destroy = EventHolder<Unit>()
        val configurationChanged = EventHolder<Configuration>()
        val keyDown = EventHolder<KeyDownEventArgs>()
        internal fun privateGetInvoker(): org.waynezhou.libutilkt.event.EventGroup<EventGroup>.Invoker {
            return super.getInvoker()
        }
    }


    private val eventGroup = EventGroup()
    fun getEventGroup(): org.waynezhou.libutilkt.event.EventGroup<EventGroup> {
        return eventGroup
    }

    private val invoker: org.waynezhou.libutilkt.event.EventGroup<EventGroup>.Invoker =
        eventGroup.privateGetInvoker()

    class KeyDownEventArgs(val keyCode: Int, val keyEvent: KeyEvent)

    protected abstract fun onInitComponents(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInitComponents(savedInstanceState)
        invoker.invoke({ it.create }, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        invoker.invoke({ it.destroy }, Unit)
    }

    override fun onResume() {
        super.onResume()
        invoker.invoke({ it.resume }, Unit)
    }

    override fun onPause() {
        super.onPause()
        invoker.invoke({ it.pause }, Unit)
    }

    override fun onStart() {
        super.onStart()
        invoker.invoke({ it.start }, Unit)
    }

    override fun onStop() {
        super.onStop()
        invoker.invoke({ it.stop }, Unit)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        invoker.invoke({ it.configurationChanged }, newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        invoker.invoke( { it.keyDown },KeyDownEventArgs(keyCode, event))
        return super.onKeyDown(keyCode, event)
    }
}
