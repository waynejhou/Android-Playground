package org.waynezhou.libutilkt.activity_result_register

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import org.waynezhou.libutilkt.event.BaseEventGroup
import org.waynezhou.libutilkt.event.EventHolder
import java.util.*

class ActivityResultRegister(
    val activity:AppCompatActivity,
    val intent: Intent
) {
    class EventGroup : BaseEventGroup<EventGroup>() {
        val result = EventHolder<ActivityResultEventArgs>()
        internal fun getPrivateInvoker() = getInvoker()
    }

    private val _eventGroup = EventGroup()
    val eventGroup: BaseEventGroup<EventGroup> get() = _eventGroup
    private val invoker: BaseEventGroup<EventGroup>.Invoker = _eventGroup.getPrivateInvoker()

    private val launcher: ActivityResultLauncher<Any> = activity.registerForActivityResult(
        object: ActivityResultContract<Any, ActivityResultEventArgs>(){
            override fun createIntent(context: Context, input: Any?): Intent {
                return intent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): ActivityResultEventArgs {
                return ActivityResultEventArgs(resultCode, intent);
            }
        }
    ) { invoker.invoke({ it.result }, it) }
    fun fire() {
        launcher.launch(null)
    }
}

data class ActivityResultEventArgs(
    val resultCode: Int = 0,
    val intent: Intent? = null
)