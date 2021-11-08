package org.waynezhou.androidplayground.audio.control

import android.media.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.waynezhou.androidplayground.audio.model.AudioModel
import org.waynezhou.androidplayground.databinding.FragmentAudioControlBinding
import org.waynezhou.libutilkt.LogHelper


class AudioControlFragment : androidx.fragment.app.Fragment() {
    private lateinit var binding: FragmentAudioControlBinding

    private val mediaPlayer = MediaPlayer()
    private val metadataRetriever = MediaMetadataRetriever()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioControlBinding.inflate(inflater, container, false)
        binding.audioControlPlayPause.setOnClickListener {
            mediaPlayer.setOnSeekCompleteListener {
                mediaPlayer.start()
                mediaPlayer.setOnSeekCompleteListener(null)
            }
            mediaPlayer.seekTo(0)
        }
        return binding.root
    }

    fun setAudio(audio: AudioModel){
        setAudio(audio.contentUri)
    }
    fun setAudio(audio: Uri){
        context?.run{
            mediaPlayer.reset()
            if(audio.scheme == "file"){
                MediaScannerConnection.scanFile(context, arrayOf(audio.path), arrayOfNulls(1)) { _, uri ->
                    LogHelper.d(uri)
                    mediaPlayer.setDataSource(this, uri)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        binding.audioControlTitle.text = audio.toString()
                        mediaPlayer.setOnPreparedListener(null)
                    }
                }
            }else{
                LogHelper.d(audio)
                mediaPlayer.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build())
                mediaPlayer.setDataSource(this, audio)
                mediaPlayer.setOnPreparedListener {
                    LogHelper.d(audio)
                    binding.audioControlTitle.text = audio.toString()
                    mediaPlayer.setOnPreparedListener(null)
                }
                mediaPlayer.prepare()
            }
            return
        }
        throw NullPointerException()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}

