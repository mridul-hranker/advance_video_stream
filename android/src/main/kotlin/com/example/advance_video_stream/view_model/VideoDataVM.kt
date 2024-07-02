package com.example.advance_video_stream.view_model

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.example.advance_video_stream.libre_tube.ProxyHelper
import com.example.advance_video_stream.libre_tube.dash.DashHelper
import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.repo.VideoDataRepo
import kotlinx.coroutines.Deferred

class VideoDataVM(private val repository: VideoDataRepo = VideoDataRepo) {

    suspend fun getData(videoId: String, useHLS: Boolean): Deferred<Streams?> {
        return repository.getData(videoId)




    }


    fun createMediaItem(streams: Streams, useHLS: Boolean): MediaItem {
        if (!useHLS) {

            val uri: Uri = if (streams.livestream && streams.dash != null) {
                ProxyHelper.unwrapStreamUrl(streams.dash).toUri()
            } else {
                val manifest: String = DashHelper.createManifest(streams, false)

                // encode to base64
                val encoded = Base64.encodeToString(manifest.toByteArray(), Base64.DEFAULT)
                Uri.parse("data:application/dash+xml;charset=utf-8;base64,$encoded")
            }

            val mediaItem = MediaItem.Builder().setUri(uri).setMimeType("application/dash+xml").build()
            return mediaItem


        } else {
            //HLS
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else hlsUri")
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else proxyUri")
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else uriMaker")
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else mediaSourceCreator")

            val mediaSourceCreator = MediaItem.Builder().setUri(Uri.parse(ProxyHelper.unwrapStreamUrl(streams.hls!!))).setMimeType("application/x-mpegURL").build()

//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else createMediaSource")

            return mediaSourceCreator;


        }
    }


}