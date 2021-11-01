package org.waynezhou.androidplayground.audio.list

import android.os.Bundle
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.waynezhou.androidplayground.databinding.FragmentAudioListBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libviewkt.RecyclerList
import org.waynezhou.androidplayground.main.AudioList
import org.waynezhou.libutilkt.LogHelper

class Fragment constructor(private val audioList:AudioList) : androidx.fragment.app.Fragment() {
    private lateinit var binding: FragmentAudioListBinding
    private var onClickListener: (View, AudioList.Audio, Int)->Unit = {_,_,_->}
    fun onClick(listener: (View, AudioList.Audio, Int)->Unit){
        onClickListener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioListBinding.inflate(inflater, container, false)
        binding.audioList.run {
            adapter = audioList.list
            layoutManager = LinearLayoutManager(this.context)
            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                val gestureDetector = GestureDetectorCompat(context, object :
                    GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
                        e?.let{ it ->
                            val childView: View? = (this@run).findChildViewUnder(it.x, it.y)
                            val idx = (this@run).children.indexOf(childView)
                            if(idx in audioList.list.indices){
                                onClickListener(childView!!, audioList.list[idx], idx)
                                LogHelper.d(audioList.list[idx])
                            }
                        }
                        return true
                    }
                })
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    gestureDetector.onTouchEvent(e)
                    return false
                }
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}