package org.waynezhou.androidplayground.main

import android.Manifest
import android.animation.Animator
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import org.waynezhou.androidplayground.audio.control.AudioControlFragment
import org.waynezhou.androidplayground.audio.list.AudioListFragment
import org.waynezhou.androidplayground.audio.model.AudioModel
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libutilkt.PermissionChecker
import org.waynezhou.libviewkt.ActivityComponent
import org.waynezhou.libviewkt.ComponentedAppCompatActivityWrapper
import org.waynezhou.libviewkt.view_transition.*


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

    override fun openAudio(audio: AudioModel) = audioControl.openAudio(audio)
    override fun openAudio(audio: Uri) = audioControl.openAudio(audio)
}

interface IAudioControl {
    fun openAudio(audio: AudioModel)
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
            LogHelper.d("$startupReason ${startupReason == startupReasons.fromUri}")
            if (startupReason == startupReasons.fromUri) {
                args.runSuperBackPress = true
            } else {
                currentLayout = layouts.audioList
                applyCurrentLayout()
            }
        }
    }

    private val fragment = AudioControlFragment()

    override fun openAudio(audio: AudioModel) {
        host.run {
            currentLayout = layouts.audioControl
            if (fragment !in supportFragmentManager.fragments) {
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
            if (fragment !in supportFragmentManager.fragments) {
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
        audioListFragment.apply {
            onAudioItemClick { view, audio, i ->
                this@AudioList.host.openAudio(audio)
            }
        }
        PermissionChecker(host, true, Manifest.permission.READ_EXTERNAL_STORAGE)
            .apply {
                eventGroup.on({ it.permissionGranted }, this@AudioList::onReadPermissionAllow)
            }.fire()
    }

    private val audioListFragment = AudioListFragment()

    private fun onReadPermissionAllow(_list: List<String>) {
        fragmentManager.beginTransaction()
            .add(binding.mainAudioList.id, audioListFragment)
            .commitNow()
    }

}