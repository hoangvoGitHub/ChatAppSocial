package com.hoangkotlin.chatappsocial.core.network.downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SocialDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) : Downloader {

    private val downloadManger = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, filename: String, mineType: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mineType)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI.and(DownloadManager.Request.NETWORK_MOBILE))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(filename)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/social/$filename")

        return downloadManger.enqueue(request)
    }
}