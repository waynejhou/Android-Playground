package org.waynezhou.androidplayground.main

import android.content.Intent
import android.net.Uri
import org.waynezhou.libutilkt.enum.EnumClass
import org.waynezhou.libutilkt.activity.ActivityComponent

internal interface IActivityStartup{
    val startupReasons: StartupReasons
    val startupReason: StartupReason
    val startupUri: Uri?
}

class Startup : ActivityComponent<MainActivity>(), IActivityStartup {
    private val intent: Intent
        get() = host.intent
    override val startupReasons = StartupReasons()
    override fun onInit() {
        host.events.on({it.start}, this::onHostStart)
        startupReason = when(intent.action){
            Intent.ACTION_MAIN -> startupReasons.fromLauncher
            Intent.ACTION_VIEW -> {
                startupUri = intent.data!!
                startupReasons.fromUri
            }
            else -> startupReasons.unknown
        }
    }

    private fun onHostStart(u:Unit) {
        host.run{
            when(startupReason){
                startupReasons.fromLauncher ->{
                    currentLayout = layouts.audioList
                }
                startupReasons.fromUri ->{
                    openAudio(startupUri!!)
                }
                else -> {
                    currentLayout = layouts.audioList
                }
            }
        }

    }

    override var startupReason: StartupReason = startupReasons.unknown
        private set

    override var startupUri: Uri? = null
        private set
}

class StartupReason constructor(statement:String): EnumClass.Int(statement)

class StartupReasons{
    val fromLauncher = StartupReason("Startup from launcher")
    val fromUri  = StartupReason("Startup from uri")
    val unknown = StartupReason("Unknown startup action")
}
