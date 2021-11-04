package org.waynezhou.androidplayground.audio.control

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import org.waynezhou.androidplayground.databinding.FragmentAudioControlBinding
import org.waynezhou.androidplayground.main.AudioList


class Fragment : androidx.fragment.app.Fragment() {
    private lateinit var binding: FragmentAudioControlBinding

    private val player = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioControlBinding.inflate(inflater, container, false)
        binding.audioControlPlayPause.setOnClickListener {
            player.setOnSeekCompleteListener {
                player.start()
                player.setOnSeekCompleteListener(null)
            }
            player.seekTo(0)
        }
        return binding.root
    }

    fun setAudio(audio: AudioList.Audio){
        context?.run{
            player.reset()
            player.setDataSource(this, audio.uri)
            player.prepareAsync()
            binding.audioControlTitle.text = audio.displayName
            return
        }
        throw NullPointerException()
    }
    fun setAudio(audio: Uri){
        context?.run{
            player.reset()
            player.setDataSource(this, audio)
            player.prepareAsync()
            binding.audioControlTitle.text = audio.toString()
            return
        }
        throw NullPointerException()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}