package org.waynezhou.androidplayground.audio.list

import android.database.Cursor
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.waynezhou.androidplayground.R
import org.waynezhou.androidplayground.audio.model.AudioModel
import org.waynezhou.androidplayground.databinding.FragmentAudioListBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libviewkt.RecyclerList
import org.waynezhou.libutilkt.LogHelper
import org.waynezhou.libviewkt.RecyclerListBinder
import org.waynezhou.libviewkt.ViewBindingRecyclerListBinder
import org.waynezhou.libviewkt.ViewDataBindingRecyclerListBinder
import java.text.DecimalFormat

class AudioListFragment : androidx.fragment.app.Fragment() {
    private lateinit var activity: AppCompatActivity
    private lateinit var binding: FragmentAudioListBinding
    private var onClickListener: (View, AudioModel, Int)->Unit = {_,_,_->}
    fun onClick(listener: (View, AudioModel, Int)->Unit){
        onClickListener = listener
    }
    fun onItemClick(holder:RecyclerView.ViewHolder){
        val idx  = holder.adapterPosition
        val audio = list[idx]
        LogHelper.d("$idx $audio")
        onClickListener(holder.itemView, audio, idx)
    }

    internal lateinit var list : RecyclerList<AudioModel, ViewBindingRecyclerListBinder<AudioModel, ItemAudioListBinding>.ViewHolder>
    private lateinit var listBinder: RecyclerListBinder<AudioModel, ViewBindingRecyclerListBinder<AudioModel, ItemAudioListBinding>.ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioListBinding.inflate(inflater, container, false)
        listBinder = /*ViewBindingRecyclerListBinder(inflater, ItemAudioListBinding::class.java) { binding, items, pos ->
            val audio = items[pos]
            binding.audioItemTextNo.text = audio.displayId
            binding.audioItemTextTitle.text = audio.displayName
            binding.audioItemTextDuration.text = formatAudioDuration(audio.duration)
        }*/
            ViewDataBindingRecyclerListBinder(inflater, ItemAudioListBinding::class.java){ holder, item ->
                LogHelper.d(item.title)
                audio = item
                viewHolder = holder
                fragment = this@AudioListFragment
            }

        binding.audioList.run {

            list = RecyclerList(requireActivity() as AppCompatActivity, listBinder)
            adapter = list
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.addAll(getAudioList())
    }

    private fun getAudioList() = sequence {
        val externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = ""
        val selectionArgs = emptyArray<String>()
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val externalCursor = activity.contentResolver.query(
            externalUri, AudioModel.projections,
            selection, selectionArgs, sortOrder
        )
        fun run(baseUri: Uri, cursor: Cursor?) = sequence {
            cursor?.run {
                while (moveToNext()) {
                    yield(AudioModel(externalUri, this))
                }
                close()
            }
        }
        for (audio in run(externalUri, externalCursor)) {
            yield(audio)
        }
    }

    companion object{
        private val audioDurationNumFormat = DecimalFormat("00")
        private fun formatAudioDuration(duration: Long): String {
            val h = audioDurationNumFormat.format(duration / 3600000);
            val m = audioDurationNumFormat.format(duration / 60000);
            val s = audioDurationNumFormat.format(duration / 1000 % 60);
            return "$h:$m:$s"
        }
    }
}