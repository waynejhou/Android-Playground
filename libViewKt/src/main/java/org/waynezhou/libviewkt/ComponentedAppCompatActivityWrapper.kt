package org.waynezhou.libviewkt

import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import org.waynezhou.libutilkt.event.BaseEventGroup
import org.waynezhou.libutilkt.event.EventHolder

@Suppress("unused")
abstract class ComponentedAppCompatActivityWrapper : AppCompatActivity() {
    class EventGroup : BaseEventGroup<EventGroup>() {
        val create = EventHolder<Bundle?>()
        val start = EventHolder<Unit>()
        val resume = EventHolder<Unit>()
        val pause = EventHolder<Unit>()
        val stop = EventHolder<Unit>()
        val destroy = EventHolder<Unit>()
        val configurationChanged = EventHolder<Configuration>()
        val keyDown = EventHolder<KeyDownEventArgs>()
        val backPressed = EventHolder<BackPressEventArgs>()
        internal fun getPrivateInvoker() = getInvoker()
    }

    private val eventGroup = EventGroup()

    val events: BaseEventGroup<EventGroup> get() = eventGroup

    private val invoker = eventGroup.getPrivateInvoker()

    class KeyDownEventArgs(val keyCode: Int, val keyEvent: KeyEvent)

    class BackPressEventArgs(var runSuperBackPress:Boolean = false)

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
        invoker.invoke({ it.keyDown }, KeyDownEventArgs(keyCode, event))
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val args = BackPressEventArgs()
        invoker.invoke({ it.backPressed }, args, {args.runSuperBackPress})
        if(args.runSuperBackPress) super.onBackPressed()
    }
}

abstract class ActivityComponent<TActivity : ComponentedAppCompatActivityWrapper> {
    var inited: Boolean = false
        private set
    protected lateinit var host: TActivity

    protected open fun onInit(){}

    fun init(activity: TActivity) {
        host = activity
        host.events.on({it.create}, this::onHostCreate)
        onInit()
        inited = true
    }

    protected open fun onHostCreate(savedInstanceState: Bundle?){}
}
