package com.hoangkotlin.chatappsocial.core.network.downloader

interface Downloader {
    fun downloadFile(url: String, filename: String, mineType: String): Long
}