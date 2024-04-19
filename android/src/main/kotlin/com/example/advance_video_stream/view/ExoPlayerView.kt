package com.example.advance_video_stream.view

import android.net.Uri
import android.util.Base64
import android.util.Log
import android.content.Context
import androidx.core.net.toUri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import com.example.advance_video_stream.libre_tube.ProxyHelper
import com.example.advance_video_stream.libre_tube.dash.DashHelper
import com.example.advance_video_stream.libre_tube.hls.YoutubeHlsPlaylistParser
import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.network.CronetHelper
import com.example.advance_video_stream.view_model.VideoDataVM
import io.flutter.view.TextureRegistry
import com.example.advance_video_stream.AdvanceVideoStreamPlugin
import android.view.Surface

@OptIn(UnstableApi::class)
class ExoPlayerView(private val context: Context) {
    private val TAG = "ExoPlayerView"
    private val exoPlayer: ExoPlayer

    init {
        Log.d(TAG, "init: called")

        val cronetDataSourceFactory = CronetDataSource.Factory(CronetHelper.cronetEngine, Executors.newCachedThreadPool())
        val dataSourceFactory = DefaultDataSource.Factory(context, cronetDataSourceFactory)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setTrackSelector(DefaultTrackSelector(context))
            .setAudioAttributes(audioAttributes, false)
            .setLoadControl(getLoadControl()).build()
    }

    fun getSurface(textureEntry: TextureRegistry.SurfaceTextureEntry?) {
        if (textureEntry != null) {
            val surface = Surface(textureEntry.surfaceTexture())
            exoPlayer.setVideoSurface(surface)
//            updatePlayerItem("21bCrsGt050", true)
        }
    }

    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            val getData =  VideoDataVM().getData(videoId)
            getData.start()
            val streams: Streams? = getData.await()
            if (streams != null) {

                if (!useHLS) {

                    val uri: Uri = if (streams.livestream && streams.dash != null) {
                        ProxyHelper.unwrapStreamUrl(streams.dash).toUri()
                    } else {
                        val manifest: String = DashHelper.createManifest(streams, false)

                        // encode to base64
                        val encoded = Base64.encodeToString(manifest.toByteArray(), Base64.DEFAULT)
                        Uri.parse("data:application/dash+xml;charset=utf-8;base64,$encoded")
                    }

                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType("application/dash+xml")
                        .build()

                    MainScope().launch {
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
//                        playerView.initialize(streams.livestream, exoPlayer)
//                        playerView.topBarTextVideoTitle.text = streams.title
                        exoPlayer.playWhenReady = true
                    }
                } else {
                    //HLS
                    val cronetDataSourceFactory = CronetDataSource.Factory(CronetHelper.cronetEngine, Executors.newCachedThreadPool())

                    val hlsMediaSourceFactory = HlsMediaSource.Factory(cronetDataSourceFactory).setPlaylistParserFactory(YoutubeHlsPlaylistParser.Factory())

                    val mediaSourceCreator = MediaItem.Builder()
                        .setUri(Uri.parse(ProxyHelper.unwrapStreamUrl(streams.hls!!)))
                        .setMimeType("application/x-mpegURL")
                        .build()

                    val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaSourceCreator)

                    MainScope().launch {
                        exoPlayer.setMediaSource(mediaSource)
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true
                    }

                }
            } else {
                Log.d(TAG, "createExoPlayer: CoroutineScope VideoDataVM().getData is null")
            }
        }
    }

    private fun getLoadControl(): LoadControl {
        return DefaultLoadControl.Builder()
            // cache the last three minutes
            .setBackBuffer(1000 * 60 * 3, true)
            /*.setBufferDurationsMs(
                MINIMUM_BUFFER_DURATION,
                bufferingGoal,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )*/.setBufferDurationsMs(
                5100,
                10000,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()
    }
}