package org.waynezhou.androidplayground.audio.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.waynezhou.androidplayground.databinding.FragmentAudioListBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libviewkt.RecyclerList
import org.waynezhou.androidplayground.main.AudioList

class Fragment constructor(private val audioList:AudioList) : androidx.fragment.app.Fragment() {
    private lateinit var binding: FragmentAudioListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.audioList.adapter = audioList.list;
        binding.audioList.layoutManager = LinearLayoutManager(this.context)
    }
}