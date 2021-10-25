package org.waynezhou.androidplayground.main

import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import org.waynezhou.androidplayground.databinding.ActivityMainBinding
import org.waynezhou.libviewkt.AppCompatActivityWrapper

class Activity : AppCompatActivityWrapper() {

    private val layout = Layout()
    override fun onInitComponents(savedInstanceState: Bundle?) {
        layout.init(this)
    }

}

class Layout{
    private lateinit var host: Activity
    private lateinit var binding: ActivityMainBinding
    internal fun init(activity:Activity){
        host = activity
        host.getEventGroup()
            .on({it.create}, this::onHostCreate)
    }

    private fun onHostCreate(savedInstanceState: Bundle?){
        binding = ActivityMainBinding.inflate(host.layoutInflater)
        host.setContentView(binding.root)
    }
}

class MediaList{
    private lateinit var host: Activity
    internal fun init(activity: Activity){
        host.getEventGroup().on({it.create}, this::onHostCreate)
    }

    private fun onHostCreate(savedInstanceState: Bundle?) {
        host.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

        )
    }
}