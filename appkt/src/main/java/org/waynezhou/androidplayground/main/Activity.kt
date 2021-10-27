package org.waynezhou.androidplayground.main

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libutilkt.PermissionChecker
import org.waynezhou.libviewkt.AppCompatActivityWrapper

class Activity : AppCompatActivityWrapper() {

    internal val layout = Layout()
    private val helloWorldButton = HelloWorldButton()
    private val audioList = AudioList()
    override fun onInitComponents(savedInstanceState: Bundle?) {
        layout.init(this)
        helloWorldButton.init(this)
        audioList.init(this)
    }

}

class Layout{
    private lateinit var host: Activity
    internal lateinit var binding: ActivityMainBinding
    internal fun init(activity:Activity){
        host = activity
        host.events
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
        host.events
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

class AudioList{
    private lateinit var host: Activity
    private lateinit var layout: Layout
    private val binding: ActivityMainBinding
        get()=layout.binding
    internal fun init(activity: Activity){
        host = activity
        host.events
            .on({it.create}, this::onHostCreate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onHostCreate(savedInstanceState: Bundle?) {
        PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE)
            .apply {
                eventGroup.on({ it.permissionGranted }, this@AudioList::onReadPermissionAllow)
            }.fire()
    }

    private fun onReadPermissionAllow(list:List<String>) {
        getAudioList()
    }

    private fun getAudioList(){
        val baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.DURATION,
        ).withIndex().associateBy ({ it.value }, {it.index})

        val cursor = host.contentResolver.query(baseUri, columns.keys.toTypedArray(),
            "${MediaStore.Audio.Media.IS_MUSIC}=1", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        cursor?.run {
            while (moveToNext()) {
                val uri = baseUri.let { Uri.withAppendedPath(it, getLong(columns[MediaStore.Audio.Media._ID]!!).toString()) }
                LogHelper.d("uri: $uri")
                columns.forEach{
                    LogHelper.d("${it.key}: ${getString(it.value)}")
                }
            }
            close()
        }
    }
}