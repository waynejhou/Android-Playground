package org.waynezhou.androidplayground.main

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libutilkt.PermissionChecker
import org.waynezhou.libviewkt.AppCompatActivityWrapper
import org.waynezhou.libviewkt.RecyclerList

class Activity : AppCompatActivityWrapper() {

    internal val layout = Layout()
    private val audioList = AudioList()
    override fun onInitComponents(savedInstanceState: Bundle?) {
        layout.init(this)
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
        binding.root.viewTreeObserver.run {
            addOnGlobalLayoutListener(this@Layout::onGlobalLayout)
        }
        host.setContentView(binding.root)
    }

    private var rootWid = -1
    private var rootHei = -1

    private fun onGlobalLayout() {
        if(binding.root.width==rootWid && binding.root.height==rootHei) return

    }
}

/*class HelloWorldButton{
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
}*/

class AudioList{
    private lateinit var host: Activity
    private lateinit var layout: Layout
    private val binding: ActivityMainBinding
        get()=layout.binding
    private lateinit var fragmentManager: FragmentManager
    internal fun init(activity: Activity){
        host = activity
        layout = host.layout
        host.events
            .on({it.create}, this::onHostCreate)
        fragmentManager = host.supportFragmentManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onHostCreate(savedInstanceState: Bundle?) {
        PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE)
            .apply {
                eventGroup.on({ it.permissionGranted }, this@AudioList::onReadPermissionAllow)
            }.fire()
    }

    lateinit var list: RecyclerList<Audio, ItemAudioListBinding>
    private val audioListFragment = org.waynezhou.androidplayground.audio.list.Fragment(this)
    private fun onReadPermissionAllow(_list:List<String>) {
        list = RecyclerList<Audio, ItemAudioListBinding>(host, ItemAudioListBinding::class.java).apply{
            onBind{ binding, source, position ->
                val audio = source[position]
                binding.audioItemTextNo.text = audio.id.toString()
                binding.audioItemTextTitle.text = audio.displayName
            }
        }
        fragmentManager.beginTransaction()
            .add(binding.mainAudioList.id,audioListFragment)
            .commitNow()
        list.addAll(getAudioList());
    }


    data class Audio(val id:Long, val uri:Uri, val displayName: String, val title: String?, val duration: Long)

    private fun getAudioList() = sequence{
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
                val id = getLong(columns[MediaStore.Audio.Media._ID]!!)
                val uri = baseUri.let { Uri.withAppendedPath(it, id.toString()) }
                val displayName = getString(columns[MediaStore.Audio.Media.DISPLAY_NAME]!!)
                val title = getStringOrNull(columns[MediaStore.Audio.Media.TITLE]!!)
                val duration = getLong(columns[MediaStore.Audio.Media.DURATION]!!)
                val audio = Audio(id, uri, displayName, title, duration)
                LogHelper.d(audio)
                yield(audio)
            }
            close()
        }
    }
}