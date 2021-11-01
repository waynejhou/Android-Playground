package org.waynezhou.androidplayground.main

import android.Manifest
import android.animation.Animator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getStringOrNull
import androidx.fragment.app.FragmentManager
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libutilkt.PermissionChecker
import org.waynezhou.libviewkt.AppCompatActivityWrapper
import org.waynezhou.libviewkt.RecyclerList
import org.waynezhou.libviewkt.view_transition.*
import java.text.DecimalFormat

class Activity : AppCompatActivityWrapper() {
    internal val layout = Layout()
    private val audioList = AudioList()
    internal val audioControl = AudioControl()
    override fun onInitComponents(savedInstanceState: Bundle?) {
        layout.init(this)
        audioList.init(this)
        audioControl.init(this)
    }

}

class AudioControl{
    private lateinit var host: Activity
    private lateinit var layout: Layout
    private val binding: ActivityMainBinding
        get() = layout.binding
    private lateinit var fragmentManager: FragmentManager
    internal fun init(activity: Activity) {
        host = activity
        layout = host.layout
        fragmentManager = host.supportFragmentManager
        host.events.on({it.backPressed}, this::onHostBackPressed)
    }

    private fun onHostBackPressed(u:Unit) {
        layout.current = layout.layoutAudioList;
        layout.refresh()
    }


    private val fragment1 = org.waynezhou.androidplayground.audio.control.Fragment()
    private val fragment2 = org.waynezhou.androidplayground.audio.control.Fragment()

    fun openAudio(audio: AudioList.Audio){
        layout.current = layout.layoutAudioControl
        when (layout.activatedAudioControl) {
            2 -> {
                layout.activatedAudioControl = 1
                layout.refresh(
                    preAction = {
                        fragmentManager.beginTransaction()
                            .add(binding.mainAudioControl1.id, fragment1)
                            .commitNow()
                        fragment1.setAudio(audio)
                    },
                    postAction = {
                        fragmentManager.beginTransaction()
                            .remove(fragment2)
                            .commitNow()
                    }
                )
            }
            1 -> {
                layout.activatedAudioControl = 2
                layout.refresh(
                    preAction = {
                        fragmentManager.beginTransaction()
                            .add(binding.mainAudioControl2.id, fragment2)
                            .commitNow()
                        fragment2.setAudio(audio)
                    },
                    postAction = {
                        fragmentManager.beginTransaction()
                            .remove(fragment1)
                            .commitNow()
                    }
                )
            }
            else -> {
                layout.activatedAudioControl = 1
                layout.refresh(
                    preAction = {
                        fragmentManager.beginTransaction()
                            .add(binding.mainAudioControl1.id, fragment1)
                            .commitNow()
                        fragment1.setAudio(audio)
                    }
                )
            }
        }
    }

}

class AudioList {
    private lateinit var host: Activity
    private lateinit var layout: Layout
    private val binding: ActivityMainBinding
        get() = layout.binding
    private lateinit var fragmentManager: FragmentManager
    internal fun init(activity: Activity) {
        host = activity
        layout = host.layout
        host.events
            .on({ it.create }, this::onHostCreate)
        fragmentManager = host.supportFragmentManager
    }

    private fun onHostCreate(savedInstanceState: Bundle?) {
        PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE)
            .apply {
                eventGroup.on({ it.permissionGranted }, this@AudioList::onReadPermissionAllow)
            }.fire()
    }

    lateinit var list: RecyclerList<Audio, ItemAudioListBinding>
    private val audioListFragment = org.waynezhou.androidplayground.audio.list.Fragment(this).apply {
        onClick{ view, audio, i ->
            this@AudioList.host.audioControl.openAudio(audio)
        }
    }
    private fun onReadPermissionAllow(_list: List<String>) {
        list = RecyclerList<Audio, ItemAudioListBinding>(
            host,
            ItemAudioListBinding::class.java
        ).apply {
            onBind { binding, source, position ->
                val audio = source[position]
                binding.audioItemTextNo.text = audio.id.toString()
                binding.audioItemTextTitle.text = audio.displayName
                binding.audioItemTextDuration.text = formatAudioDuration(audio.duration)
            }
        }
        fragmentManager.beginTransaction()
            .add(binding.mainAudioList.id, audioListFragment)
            .commitNow()
        list.addAll(getAudioList())
    }


    data class Audio(
        val id: Long,
        val uri: Uri,
        val displayName: String,
        val title: String?,
        val duration: Long
    )

    private fun getAudioList() = sequence {
        val baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.DURATION,
        ).withIndex().associateBy({ it.value }, { it.index })

        val cursor = host.contentResolver.query(
            baseUri, columns.keys.toTypedArray(),
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

    companion object {
        private val audioDurationNumFormat = DecimalFormat("00")
        private fun formatAudioDuration(duration: Long): String {
            val h = audioDurationNumFormat.format(duration / 3600000);
            val m = audioDurationNumFormat.format(duration / 60000);
            val s = audioDurationNumFormat.format(duration / 1000 % 60);
            return "$h:$m:$s"
        }
    }
}