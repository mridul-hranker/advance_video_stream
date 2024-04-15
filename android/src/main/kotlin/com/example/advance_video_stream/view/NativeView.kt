package com.example.advance_video_stream.view

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.advance_video_stream.R
import com.example.advance_video_stream.libre_tube.ProxyHelper
import com.example.advance_video_stream.libre_tube.dash.DashHelper
import com.example.advance_video_stream.libre_tube.hls.YoutubeHlsPlaylistParser
import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.libre_tube.setMetadata
import com.example.advance_video_stream.network.CronetHelper
import com.example.advance_video_stream.view_model.VideoDataVM
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(UnstableApi::class)
class NativeView(context: Context, id: Int, creationParams: Map<String?, Any?>?) : PlatformView {
    private val TAG = "NativeView"

    private val playerView: CustomPlayerView
    private val exoPlayer: ExoPlayer

    override fun getView(): View = playerView

    init {
        playerView = LayoutInflater.from(context).inflate(R.layout.custom_exo_player, null) as CustomPlayerView

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

        playerView.player = exoPlayer
    }

    override fun dispose() {
        exoPlayer.stop()
        exoPlayer.release()
        android.util.Log.d(TAG, "dispose: called")
    }


    //Player vars
    private val MINIMUM_BUFFER_DURATION = 1000 * 10 // exo default is 50s

    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            val streams: Streams? = VideoDataVM().getData(videoId)
            if (streams != null) {

                if (!useHLS) {
                    val manifest: String = DashHelper.createManifest(streams, false)

                    // encode to base64
                    val encoded = Base64.encodeToString(manifest.toByteArray(), Base64.DEFAULT)
                    val uri: Uri = Uri.parse("data:application/dash+xml;charset=utf-8;base64,$encoded")

                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType("application/dash+xml")
                        .setMetadata(streams)
                        .build()

                    MainScope().launch {
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
                        playerView.initialize(false, exoPlayer)
                        exoPlayer.playWhenReady = true
                    }
                } else {
                    //HLS
                    val cronetDataSourceFactory = CronetDataSource.Factory(
                        CronetHelper.cronetEngine,
                        Executors.newCachedThreadPool()
                    )

                    val hlsMediaSourceFactory = HlsMediaSource.Factory(cronetDataSourceFactory).setPlaylistParserFactory(YoutubeHlsPlaylistParser.Factory())

                    val mediaSource = hlsMediaSourceFactory.createMediaSource(
                        MediaItem.Builder()
                            .setUri(Uri.parse(ProxyHelper.unwrapStreamUrl(streams.hls!!)))
                            .setMimeType("application/x-mpegURL")
                            .setMetadata(streams)
                            .build()
                    )

                    MainScope().launch {
                        exoPlayer.setMediaSource(mediaSource)
                        exoPlayer.prepare()
                        playerView.initialize(false, exoPlayer)
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

    private val bufferingGoal: Int
        get() = ("80").toInt() * 1000

}