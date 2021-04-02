package com.hinnka.tsbrowser.download

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.hinnka.tsbrowser.ext.mimeType
import zlc.season.rxdownload4.file
import java.io.File

class OpenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != action) return
        val url = intent.getStringExtra("url") ?: return
//        val publicDir = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
        val file = url.file()
        println("TSBrowser file exist and canRead: ${file.exists()} ${file.canRead()}")
        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, file.mimeType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(openIntent)
    }

    companion object {

        private val receiver = OpenReceiver()
        private const val action = "com.hinnka.action.OPEN_FILE"

        fun register(context: Context) {
            val filter = IntentFilter(action)
            try {
                context.registerReceiver(receiver, filter)
            } catch (e: Exception) {
            }
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getPendingIntent(context: Context, url: String): PendingIntent {
            val intent = Intent(action).apply {
                putExtra("url", url)
                setPackage(context.packageName)
            }
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}