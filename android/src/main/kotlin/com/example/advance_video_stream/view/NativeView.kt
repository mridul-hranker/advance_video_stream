package com.example.advance_video_stream.view

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.advance_video_stream.libre_tube.DashHelper
import com.example.advance_video_stream.libre_tube.Streams
import com.example.advance_video_stream.libre_tube.setMetadata
import com.example.advance_video_stream.network.CronetHelper
import com.example.advance_video_stream.viewModel.VideoDataVM
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(UnstableApi::class)
internal class NativeView(context: Context, id: Int, creationParams: Map<String?, Any?>?) : PlatformView {
    private val TAG = "NativeView"

    private val playerView: PlayerView
    private val exoPlayer: ExoPlayer

    override fun getView(): View {
        return playerView
    }

    init {
        playerView = PlayerView(context)


        val cronetDataSourceFactory = CronetDataSource.Factory(
            CronetHelper.cronetEngine,
            Executors.newCachedThreadPool()
        )
        val dataSourceFactory = DefaultDataSource.Factory(context, cronetDataSourceFactory)

        exoPlayer = ExoPlayer.Builder(context).setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory)).setLoadControl(getLoadControl()).build()
        playerView.player = exoPlayer

        CoroutineScope(Dispatchers.IO).launch {
            val streams: Streams? = VideoDataVM().getData("3UJ_mERvw3A")
            if (streams != null) {
                this@NativeView.streams = streams
                updatePlayerItem()
            } else {
                Log.d(TAG, "createExoPlayer: CoroutineScope VideoDataVM().getData is null")
            }
        }
    }

    override fun dispose() {}


    //Player vars
    lateinit var uri: Uri
    lateinit var mimeType: String
    lateinit var streams: Streams
    private val MINIMUM_BUFFER_DURATION = 1000 * 10 // exo default is 50s

    private fun updatePlayerItem() {

        val manifest: String = DashHelper.createManifest(streams, false)

        // encode to base64
        val encoded = Base64.encodeToString(manifest.toByteArray(), Base64.DEFAULT)
        uri = Uri.parse("data:application/dash+xml;charset=utf-8;base64,$encoded")

        mimeType = "application/dash+xml"

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(mimeType)
            .setMetadata(streams)
            .build()

        MainScope().launch {
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.play()
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