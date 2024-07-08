package com.example.advance_video_stream.view

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.advance_video_stream.libre_tube.hls.YoutubeHlsPlaylistParser
import com.example.advance_video_stream.models.QueuingEventSink
import com.example.advance_video_stream.network.CronetHelper
import com.example.advance_video_stream.new_pipe_extractor.NewPipeExtractorHelper
import com.example.advance_video_stream.view_model.VideoDataVM
import io.flutter.plugin.common.EventChannel
import io.flutter.view.TextureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.util.Arrays
import java.util.concurrent.Executors

@OptIn(UnstableApi::class)
class ExoPlayerView(private val context: Context, private val textureEntry: TextureRegistry.SurfaceTextureEntry, private val eventChannel: EventChannel) {
    private val TAG = "ExoPlayerView"
    private val exoPlayer: ExoPlayer

    val videoDataVM = VideoDataVM()
    private var eventSink: QueuingEventSink = QueuingEventSink()
    private val runnableHandler = Handler(Looper.getMainLooper())

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

        Log.d(TAG, "init: eventChannel $eventChannel")
        Log.d(TAG, "init: eventSink $eventSink")

        eventChannel.setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(o: Any?, sink: EventChannel.EventSink) {
                    eventSink.setDelegate(sink)
                    Log.d(TAG, "onListen: setStreamHandler onListen called")
                }

                override fun onCancel(o: Any?) {
                    eventSink.setDelegate(null)
                    Log.d(TAG, "onCancel: setStreamHandler onCancel called")
                }
            })

//        updatePlayerItem("21bCrsGt050", true)

        exoPlayer.addListener(object : Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)

                Log.d(TAG, "onPlaybackStateChanged: bufferedPosition ${exoPlayer.bufferedPosition}")
                Log.d(TAG, "onPlaybackStateChanged: bufferedPercentage ${exoPlayer.bufferedPercentage}")
                Log.d(TAG, "onPlaybackStateChanged: contentBufferedPosition ${exoPlayer.contentBufferedPosition}")
                Log.d(TAG, "onPlaybackStateChanged: totalBufferedDuration ${exoPlayer.totalBufferedDuration}")

                if (playbackState == Player.STATE_BUFFERING) {
                    setBuffering(true)
//                sendBufferingUpdate()
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_BUFFERING called")

//                    val range: List<Long> = listOf(0, exoPlayer.bufferedPosition)

//                    range.forEach {
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_BUFFERING bufferedPosition ${exoPlayer.bufferedPosition}")
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_BUFFERING bufferedPercentage ${exoPlayer.bufferedPercentage}")
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_BUFFERING contentBufferedPosition ${exoPlayer.contentBufferedPosition}")
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_BUFFERING totalBufferedDuration ${exoPlayer.totalBufferedDuration}")
//                    }

                } else if (playbackState == Player.STATE_READY) {
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_READY called")
//                if (!isInitialized) {
//                    isInitialized = true
                    sendInitialized()
//                }
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_READY called")
                } else if (playbackState == Player.STATE_ENDED) {
                    val event: MutableMap<String, Any> = HashMap()
                    event["event"] = "completed"
                    eventSink.success(event)
                    Log.d(TAG, "onPlaybackStateChanged: playbackState == Player.STATE_ENDED called")
                }

                if (playbackState != Player.STATE_BUFFERING) {
//                setBuffering(false)
                    Log.d(TAG, "onPlaybackStateChanged: playbackState != Player.STATE_BUFFERING called")
                }


            }
        })
    }


    fun setBuffering(buffering: Boolean) {
//        if (isBuffering != buffering) {
//            isBuffering = buffering
        val event: MutableMap<String, Any> = java.util.HashMap()
        event["event"] = /*if (isBuffering) "bufferingStart" else*/ "bufferingEnd"
        eventSink.success(event)
//        }
    }


    fun sendBufferingUpdate() {
        val event: MutableMap<String, Any> = HashMap()
        event["event"] = "bufferingUpdate"
        val range: List<Number?> = listOf(0, exoPlayer.bufferedPosition)
        // iOS supports a list of buffered ranges, so here is a list with a single range.
        event["values"] = listOf(range)
        eventSink?.success(event)
    }


    fun sendInitialized() {
        Log.d(TAG, "sendInitialized: called")
        val event: MutableMap<String, Any> = HashMap()
        event["event"] = "initialized"
        event["duration"] = exoPlayer.duration

        if (exoPlayer.videoFormat != null) {
            val videoFormat: Format? = exoPlayer.videoFormat
            var width: Int = videoFormat?.width ?: 0
            var height: Int = videoFormat?.height ?: 0
            val rotationDegrees: Int = videoFormat?.rotationDegrees ?: 0
            // Switch the width/height if video was taken in portrait mode
            if (rotationDegrees == 90 || rotationDegrees == 270) {
                width = exoPlayer.videoFormat!!.height
                height = exoPlayer.videoFormat!!.width
            }
            event["width"] = width
            event["height"] = height

            // Rotating the video with ExoPlayer does not seem to be possible with a Surface,
            // so inform the Flutter code that the widget needs to be rotated to prevent
            // upside-down playback for videos with rotationDegrees of 180 (other orientations work
            // correctly without correction).
            if (rotationDegrees == 180) {
                event["rotationCorrection"] = rotationDegrees
            }
        }

        eventSink.success(event)
//        runnableHandler.postDelayed(this::sendInitialized, 100)
    }


    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {

            Log.d(TAG, "updatePlayerItem: videoId $videoId useHLS $useHLS")

            NewPipeExtractorHelper.getStreamingService()

            val streamingExtractor: Deferred<StreamInfo?> = async {
                return@async NewPipeExtractorHelper.getStreamInfo(videoId)
            }

            val streamUrl: StreamInfo? = streamingExtractor.await()

            if (streamUrl != null) {

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
                    exoPlayer.playWhenReady = true
                }
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