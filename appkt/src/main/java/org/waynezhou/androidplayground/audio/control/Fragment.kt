package org.waynezhou.androidplayground.audio.control

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import org.waynezhou.androidplayground.databinding.FragmentAudioControlBinding


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

    fun setAudio(uri: Uri){
        context?.run{
            player.reset()
            player.setDataSource(this, uri)
            player.prepareAsync()
            return
        }
        throw NullPointerException()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}