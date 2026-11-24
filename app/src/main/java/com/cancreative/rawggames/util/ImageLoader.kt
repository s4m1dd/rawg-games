package com.cancreative.rawggames.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// shortcut: memory cache only; a restart refetches everything.
object ImageLoader {

    private val cache = object : LruCache<String, Bitmap>(8 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun load(url: String?, view: ImageView) {
        view.tag = url
        view.setImageDrawable(null)
        if (url.isNullOrBlank()) return
        cache.get(url)?.let {
            view.setImageBitmap(it)
            return
        }
        scope.launch {
            val bitmap = runCatching { download(url) }.getOrNull() ?: return@launch
            cache.put(url, bitmap)
            withContext(Dispatchers.Main) { if (view.tag == url) view.setImageBitmap(bitmap) }
        }
    }

    private fun download(url: String): Bitmap? {
        val conn = URL(url).openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = 15_000
            conn.readTimeout = 15_000
            conn.inputStream.use { BitmapFactory.decodeStream(it) }
        } finally {
            conn.disconnect()
        }
    }
}
