package org.waynezhou.androidplayground.audio.control

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import org.waynezhou.androidplayground.audio.model.AudioModel
import org.waynezhou.androidplayground.databinding.FragmentAudioControlBinding
import org.waynezhou.libutilkt.LogHelper


class AudioControlFragment : androidx.fragment.app.Fragment() {
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

    fun setAudio(audio: AudioModel){
        context?.run{
            player.reset()
            player.setDataSource(this, audio.contentUri)
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