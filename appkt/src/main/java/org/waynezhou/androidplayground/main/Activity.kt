package org.waynezhou.androidplayground.main

import android.Manifest
import android.animation.Animator
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getStringOrNull
import androidx.fragment.app.FragmentManager
import org.waynezhou.androidplayground.R
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libutilkt.PermissionChecker
import org.waynezhou.libviewkt.ActivityComponent
import org.waynezhou.libviewkt.ComponentedAppCompatActivityWrapper
import org.waynezhou.libviewkt.RecyclerList
import org.waynezhou.libviewkt.view_transition.*
import java.text.DecimalFormat

class Activity : ComponentedAppCompatActivityWrapper(), IActivityStartup, IActivityLayout,
    IAudioControl {
    private val startup = Startup()
    private val layout = Layout()
    private val audioList = AudioList()
    private val audioControl = AudioControl()
    override fun onInitComponents(savedInstanceState: Bundle?) {
        startup.init(this)
        layout.init(this)
        audioList.init(this)
        audioControl.init(this)
    }

    override val startupReasons: StartupReasons
        get() = startup.startupReasons
    override val startupReason: StartupReason
        get() = startup.startupReason
    override val startupUri: Uri?
        get() = startup.startupUri
    override val layouts: Layouts
        get() = layout.layouts
    override val binding: ActivityMainBinding
        get() = layout.binding

    override fun applyCurrentLayout(
        args: ViewAnimatorArgs,
        preAction: (Animator) -> Unit,
        postAction: (Animator) -> Unit
    ) = layout.applyCurrentLayout(args, preAction, postAction)

    override var currentLayout: ViewTransition<Layouts>
        get() = layout.currentLayout
        set(value) {
            layout.currentLayout = value
        }

    override fun openAudio(audio: AudioList.Audio) = audioControl.openAudio(audio)
    override fun openAudio(audio: Uri) = audioControl.openAudio(audio)
}

interface IAudioControl {
    fun openAudio(audio: AudioList.Audio)
    fun openAudio(audio: Uri)
}

class AudioControl : ActivityComponent<Activity>(), IAudioControl {
    private val binding: ActivityMainBinding
        get() = host.binding

    override fun onHostCreate(savedInstanceState: Bundle?) {
        host.events.on({ it.backPressed }, this::onHostBackPressed)
    }

    private fun onHostBackPressed(args: ComponentedAppCompatActivityWrapper.BackPressEventArgs) {
        host.run {
            if(startupReason==startupReasons.fromUri){
                args.runSuperBackPress = true
            }else{
                currentLayout = layouts.audioList
                applyCurrentLayout()
            }
        }
    }

    private val fragment = org.waynezhou.androidplayground.audio.control.Fragment()

    override fun openAudio(audio: AudioList.Audio) {
        host.run {
            currentLayout = layouts.audioControl
            if( fragment !in supportFragmentManager.fragments){
                supportFragmentManager.beginTransaction()
                    .add(binding.mainAudioControl.id, fragment)
                    .commitNow()
            }
            fragment.setAudio(audio)
            host.applyCurrentLayout()

        }
    }
    override fun openAudio(audio: Uri) {
        host.run {
            currentLayout = layouts.audioControl
            if( fragment !in supportFragmentManager.fragments){
                supportFragmentManager.beginTransaction()
                    .add(binding.mainAudioControl.id, fragment)
                    .commitNow()
            }
            fragment.setAudio(audio)
            host.applyCurrentLayout()

        }
    }
}

class AudioList : ActivityComponent<Activity>() {
    private val binding: ActivityMainBinding
        get() = host.binding
    private val fragmentManager: FragmentManager
        get() = host.supportFragmentManager

    override fun onHostCreate(savedInstanceState: Bundle?) {
        PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE)
            .apply {
                eventGroup.on({ it.permissionGranted }, this@AudioList::onReadPermissionAllow)
            }.fire()
    }

    lateinit var list: RecyclerList<Audio, ItemAudioListBinding>
    private val audioListFragment =
        org.waynezhou.androidplayground.audio.list.Fragment(this).let {
            it.onClick { view, audio, i ->
                this.host.openAudio(audio)
            }
            it
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

        host.contentResolver.refresh(baseUri, null, null)
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