package org.waynezhou.androidplayground.main

import android.content.ContentResolver
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libviewkt.AppCompatActivityWrapper

class Activity : AppCompatActivityWrapper() {

    internal val layout = Layout()
    internal val helloWorldButton = HelloWorldButton()
    internal val mediaList = MediaList()
    override fun onInitComponents(savedInstanceState: Bundle?) {
        layout.init(this)
        helloWorldButton.init(this)
        mediaList.init(this)
    }

}

class Layout{
    private lateinit var host: Activity
    internal lateinit var binding: ActivityMainBinding
    internal fun init(activity:Activity){
        host = activity
        host.getEventGroup()
            .on({it.create}, this::onHostCreate)
    }

    private fun onHostCreate(savedInstanceState: Bundle?){
        binding = ActivityMainBinding.inflate(host.layoutInflater)
        host.setContentView(binding.root)
    }
}

class HelloWorldButton{
    private lateinit var host: Activity
    private lateinit var layout: Layout
    private val binding: ActivityMainBinding
        get()=layout.binding
    internal fun init(activity:Activity){
        host = activity
        host.getEventGroup()
            .on({it.create}, this::onHostCreate)
        layout = host.layout
    }
    private fun onHostCreate(savedInstanceState: Bundle?){
        LogHelper.i()
        binding.mainHelloBtn.setOnClickListener{
            LogHelper.d("Hello World")
        }
    }
}

class MediaList{
    private lateinit var host: Activity
    internal fun init(activity: Activity){
        host = activity
        host.getEventGroup().on({it.create}, this::onHostCreate)
    }

    private fun onHostCreate(savedInstanceState: Bundle?) {
        val cancleSignal = CancellationSignal()
        val cursor = host.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, Bundle.EMPTY, cancleSignal)
        LogHelper.d("%b", cursor==null)
        cursor?.run {
            LogHelper.d(cursor.columnNames.joinToString(", "))
            while(moveToNext().apply { LogHelper.d("Has Next: %b", this); }){
                LogHelper.d(cursor.columnNames.joinToString(", "))
            }
            close()
        }

    }
}