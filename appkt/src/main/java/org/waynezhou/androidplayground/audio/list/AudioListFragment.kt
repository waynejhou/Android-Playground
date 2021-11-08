package org.waynezhou.androidplayground.audio.list

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.waynezhou.androidplayground.audio.model.AudioModel
import org.waynezhou.androidplayground.databinding.FragmentAudioListBinding
import org.waynezhou.androidplayground.databinding.ItemAudioListBinding
import org.waynezhou.libviewkt.RecyclerList
import org.waynezhou.libviewkt.RecyclerListBinder
import org.waynezhou.libviewkt.ViewBindingRecyclerListBinder
import org.waynezhou.libviewkt.ViewDataBindingRecyclerListBinder
import java.text.DecimalFormat

typealias AudioModelViewHolder = ViewBindingRecyclerListBinder<AudioModel, ItemAudioListBinding>.ViewHolder
typealias AudioModelList = RecyclerList<AudioModel, AudioModelViewHolder>
typealias AudioModelListBinder = RecyclerListBinder<AudioModel, AudioModelViewHolder>
typealias AudioModelListDataBinder = ViewDataBindingRecyclerListBinder<AudioModel, ItemAudioListBinding>

class AudioListFragment : androidx.fragment.app.Fragment() {
    private lateinit var activity: AppCompatActivity
    private lateinit var binding: FragmentAudioListBinding
    private var onClickListener: (View, AudioModel, Int)->Unit = {_,_,_->}
    fun onAudioItemClick(listener: (View, AudioModel, Int)->Unit){
        onClickListener = listener
    }

    private val audioItemEventHandler = object : IItemAudioListEventHandler{
        override fun onAudioItemClick(view: View) {
            val vh = binding.audioList.findContainingViewHolder(view)!!
            val idx  = vh.adapterPosition
            val audio = list[idx]
            onClickListener(view, audio, idx)
        }
    }

    internal lateinit var list : AudioModelList
    private lateinit var listBinder: AudioModelListBinder
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
        listBinder = AudioModelListDataBinder(inflater, ItemAudioListBinding::class.java).apply{
                onCreate {
                    eventHolder = audioItemEventHandler
                    executePendingBindings()
                }
                onBind { item, holder -> audio = item ; adapterPosition = holder.adapterPosition}
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
            val h = audioDurationNumFormat.format(duration / 3600000)
            val m = audioDurationNumFormat.format(duration / 60000)
            val s = audioDurationNumFormat.format(duration / 1000 % 60)
            return "$h:$m:$s"
        }
    }
}