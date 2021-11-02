package org.waynezhou.androidplayground.main

import android.content.Intent
import android.os.Bundle
import org.waynezhou.libutilkt.LogHelper

interface IStartup{
}

class Startup : IStartup {
    private lateinit var host: Activity
    private lateinit var intent: Intent
    internal fun init(activity: Activity){
        host = activity;
        intent = host.intent;
        host.events.on({it.create}, this::onHostCreate)
    }

    private fun onHostCreate(savedInstanceState: Bundle?) {
        LogHelper.d(intent)
    }
}