package com.example.advance_video_stream.view

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.OptIn
import androidx.core.net.toUri
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
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.advance_video_stream.R
import com.example.advance_video_stream.libre_tube.ProxyHelper
import com.example.advance_video_stream.libre_tube.dash.DashHelper
import com.example.advance_video_stream.libre_tube.hls.YoutubeHlsPlaylistParser
import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.network.CronetHelper
import com.example.advance_video_stream.view_model.VideoDataVM
import io.flutter.plugin.platform.PlatformView
import io.flutter.view.TextureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import android.view.Surface
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait


@OptIn(UnstableApi::class)
class NativeView(context: Context, id: Int, creationParams: Map<String?, Any?>?) : PlatformView {
    private val TAG = "NativeView"

    private val playerView: CustomPlayerView
    private val exoPlayer: ExoPlayer

    override fun getView(): View = playerView

    init {
        Log.d(TAG, "init: called")
        playerView = LayoutInflater.from(context).inflate(R.layout.custom_exo_player, null) as CustomPlayerView

        val cronetDataSourceFactory = CronetDataSource.Factory(CronetHelper.cronetEngine, Executors.newCachedThreadPool())
        val dataSourceFactory = DefaultDataSource.Factory(context, cronetDataSourceFactory)

        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build()

        exoPlayer = ExoPlayer.Builder(context).setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory)).setTrackSelector(DefaultTrackSelector(context))
            .setAudioAttributes(audioAttributes, false).setLoadControl(getLoadControl()).build()

        playerView.player = exoPlayer
    }

    override fun dispose() {
        exoPlayer.stop()
        exoPlayer.release()
        Log.d(TAG, "dispose: called")
    }

    //Player vars
    private val MINIMUM_BUFFER_DURATION = 1000 * 10 // exo default is 50s

    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
//            Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO called")
            val getData = VideoDataVM().getData(videoId)
            getData.start()
//            Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO getData $getData")
            val streams: Streams? = getData.await()
//            Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO streams $streams")
//            Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO streams hls ${streams?.hls}")
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

                    val mediaItem = MediaItem.Builder().setUri(uri).setMimeType("application/dash+xml").build()

                    MainScope().launch {
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
                        playerView.initialize(streams.livestream, exoPlayer)
                        playerView.topBarTextVideoTitle.text = streams.title
                        exoPlayer.playWhenReady = true
                    }
                } else {
                    //HLS
                    val cronetDataSourceFactory = CronetDataSource.Factory(CronetHelper.cronetEngine, Executors.newCachedThreadPool())

                    val hlsMediaSourceFactory = HlsMediaSource.Factory(cronetDataSourceFactory).setPlaylistParserFactory(YoutubeHlsPlaylistParser.Factory())

//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else hlsUri")
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else proxyUri")
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else uriMaker")
//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else mediaSourceCreator")

                    val mediaSourceCreator =
                        MediaItem.Builder().setUri(Uri.parse(ProxyHelper.unwrapStreamUrl(streams.hls!!))).setMimeType("application/x-mpegURL").build()

//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else createMediaSource")

                    val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaSourceCreator)

//                    Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else called")
                    MainScope().launch {
//                        Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else MainScope called")
                        exoPlayer.setMediaSource(mediaSource)
                        exoPlayer.prepare()
                        playerView.initialize(streams.livestream, exoPlayer)
                        playerView.topBarTextVideoTitle.text = streams.title
                        exoPlayer.playWhenReady = true
//                        Log.i(TAG, "updatePlayerItem: CoroutineScope Dispatchers.IO else MainScope called post")
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
            .setBackBuffer(1000 * 60 * 3, true)/*.setBufferDurationsMs(
                MINIMUM_BUFFER_DURATION,
                bufferingGoal,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )*/.setBufferDurationsMs(
                5100, 10000, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            ).build()
    }

    private val bufferingGoal: Int
        get() = ("80").toInt() * 1000

    fun getPosition(): Long = exoPlayer.currentPosition

    fun pause() = exoPlayer.pause()

    fun play() = exoPlayer.play()

    fun setPosition(position: Long) = exoPlayer.seekTo(position)

    fun setOrientationAspectRatio(isLandscape: Boolean) {
        playerView.resizeMode = if (isLandscape) {
            AspectRatioFrameLayout.RESIZE_MODE_FILL
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FILL
        }
    }
}