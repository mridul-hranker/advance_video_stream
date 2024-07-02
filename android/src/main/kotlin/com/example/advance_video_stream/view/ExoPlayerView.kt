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
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
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
import com.example.advance_video_stream.new_pipe_extractor.NewPipeExtractorHelper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamType

@OptIn(UnstableApi::class)
class ExoPlayerView(private val context: Context, private val textureEntry: TextureRegistry.SurfaceTextureEntry) {
    private val TAG = "ExoPlayerView"
    private val exoPlayer: ExoPlayer

    val videoDataVM = VideoDataVM()

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

        val surface = Surface(textureEntry.surfaceTexture())
        exoPlayer.setVideoSurface(surface)

//        updatePlayerItem("21bCrsGt050", true)

        exoPlayer.addListener(object : Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
//                setBuffering(true)
//                sendBufferingUpdate()
                    Log.d(TAG, "onPlayerStateChanged: playbackState == Player.STATE_BUFFERING called")

                    val range: List<Long> = listOf(0, exoPlayer.bufferedPosition)

                    range.forEach {
                        Log.d(TAG, "onPlayerStateChanged: playbackState == Player.STATE_BUFFERING range $it")
                    }

                } else if (playbackState == Player.STATE_READY) {
//                if (!isInitialized) {
//                    isInitialized = true
//                    sendInitialized()
//                }
                    Log.d(TAG, "onPlayerStateChanged: playbackState == Player.STATE_READY called")
                } else if (playbackState == Player.STATE_ENDED) {
                    val event: MutableMap<String, Any> = HashMap()
                    event["event"] = "completed"
//                eventSink.success(event)
                    Log.d(TAG, "onPlayerStateChanged: playbackState == Player.STATE_ENDED called")
                }

                if (playbackState != Player.STATE_BUFFERING) {
//                setBuffering(false)
                    Log.d(TAG, "onPlayerStateChanged: playbackState != Player.STATE_BUFFERING called")
                }
            }
        })
    }

    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {



            Log.d(TAG, "updatePlayerItem: videoId $videoId useHLS $useHLS")

            NewPipeExtractorHelper.getStreamingService()

            val streamingExtractor: Deferred<StreamInfo> = async {
                return@async NewPipeExtractorHelper.getStreamInfo(videoId)
            }

            val streamUrl: StreamInfo = streamingExtractor.await()

            val cronetDataSourceFactory = CronetDataSource.Factory(CronetHelper.cronetEngine, Executors.newCachedThreadPool())
            val hlsMediaSourceFactory = HlsMediaSource.Factory(cronetDataSourceFactory).setPlaylistParserFactory(YoutubeHlsPlaylistParser.Factory())

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(streamUrl.hlsUrl))
                .setMimeType("application/x-mpegURL")
                .build()

            val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaItem)

            MainScope().launch {
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
//                playerView.initialize(streamUrl.streamType == StreamType.LIVE_STREAM, exoPlayer)
//                playerView.topBarTextVideoTitle.text = streamUrl.name
                exoPlayer.playWhenReady = true
            }


            /*val getData = videoDataVM.getData(videoId, useHLS)
            getData.start()
            val streams: Streams? = getData.await()
            if (streams != null) {

                val mediaItem: MediaItem = videoDataVM.createMediaItem(streams, useHLS)

                if (!useHLS) {

                    MainScope().launch {
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true
                    }
                } else {
                    //HLS
                    val cronetDataSourceFactory = CronetDataSource.Factory(CronetHelper.cronetEngine, Executors.newCachedThreadPool())

                    val hlsMediaSourceFactory = HlsMediaSource.Factory(cronetDataSourceFactory).setPlaylistParserFactory(YoutubeHlsPlaylistParser.Factory())

                    val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaItem)

                    MainScope().launch {
                        exoPlayer.setMediaSource(mediaSource)
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true
                    }

                }
            } else {
                Log.d(TAG, "createExoPlayer: CoroutineScope VideoDataVM().getData is null")
            }*/
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

    fun playPlayer() {
        exoPlayer.playWhenReady = true;
    }

    fun pausePlayer() {
        exoPlayer.playWhenReady = false;
    }

    fun dispose() {
        exoPlayer.stop()
        exoPlayer.release()
        Log.d(TAG, "dispose: called")
    }
}