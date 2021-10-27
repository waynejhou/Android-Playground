package org.waynezhou.libutilkt

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import org.waynezhou.libutilkt.event.BaseEventGroup
import org.waynezhou.libutilkt.event.EventHolder


class PermissionChecker constructor(
    private val activity: ComponentActivity,
    private val tryGrantPermissionOnDenied: Boolean,
    vararg permissions: String,
) {
    class EventGroup : BaseEventGroup<EventGroup>() {
        val permissionGranted = EventHolder<List<String>>()
        val permissionDenied = EventHolder<List<String>>()
        internal fun getPrivateInvoker() = getInvoker()
    }

    private val _eventGroup = EventGroup()
    val eventGroup: BaseEventGroup<EventGroup> get() = _eventGroup
    private val invoker: BaseEventGroup<EventGroup>.Invoker = _eventGroup.getPrivateInvoker()

    private val permissions = arrayOf(*permissions)
    private val grantPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { x -> x.value }) {
            invoker.invoke({ it.permissionGranted }, result.keys.toList())
        } else {
            invoker.invoke({ it.permissionDenied }, result.filterValues { !it }.keys.toList())
        }
    }

    fun fire() {
        val result = permissions.associateWith {
            ContextCompat.checkSelfPermission(
                activity,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (result.all { x -> x.value }) {
            invoker.invoke({ it.permissionGranted }, result.keys.toList())
        } else {
            if (tryGrantPermissionOnDenied) {
                grantPermissionLauncher.launch(permissions)
            } else {
                invoker.invoke({ it.permissionDenied }, result.filterValues { !it }.keys.toList())
            }
        }
    }

}