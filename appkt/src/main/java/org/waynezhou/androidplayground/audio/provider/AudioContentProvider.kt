package org.waynezhou.androidplayground.audio.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.storage.StorageManager
import android.util.Log
import org.waynezhou.libutilkt.LogHelper
import java.io.File
import java.nio.file.Files

class AudioContentProvider : ContentProvider() {

    companion object{
        const val databaseName = "org.waynezhou.androidplayground.audio.database"
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Implement this to handle requests to insert a new row.")
    }


    override fun onCreate(): Boolean {
        val storageManager = context!!.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        fun recursiveScan(dir:File):Sequence<File> = sequence {
            yield(dir)
            LogHelper.d(dir.listFiles())
            dir.listFiles()?.forEach {
                yieldAll(recursiveScan(it))
            }
        }
        storageManager.storageVolumes.map{ File("/storage/${if(it.isEmulated){"emulated/0"}else{it.uuid}}/Music")}.forEach{
            LogHelper.d(it)
            recursiveScan(it).forEach {

            }
        }
        /*
        val androidStoragePath = System.getenv("ANDROID_STORAGE")!!
        val androidStorageDir = File(androidStoragePath)
        LogHelper.d(androidStorageDir)
        androidStorageDir.listFiles()?.forEach {
            it.run{ LogHelper.d("$this $isDirectory $canonicalPath")}
            it.listFiles()?.forEach {
                it.run{ LogHelper.d("  $this $isDirectory $canonicalPath")}
            }
        }*/

        TODO("Implement this to initialize your content provider on startup.")
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        TODO("Implement this to handle query requests from clients.")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}