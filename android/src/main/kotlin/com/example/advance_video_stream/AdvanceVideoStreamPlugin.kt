package com.example.advance_video_stream

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.annotation.NonNull
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.advance_video_stream.libre_tube.dash.DashHelper

import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.libre_tube.setMetadata
import com.example.advance_video_stream.view.NativeViewFactory
import com.example.advance_video_stream.viewModel.VideoDataVM

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/** AdvanceVideoStreamPlugin */
class AdvanceVideoStreamPlugin : FlutterPlugin, MethodCallHandler {
    private val TAG = "AdvanceVideoStreamPl"

    companion object {
        var context: Context? = null
    }

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel


    //Player vars
    private lateinit var exoPlayer: ExoPlayer
    lateinit var uri: Uri
    lateinit var mimeType: String
    lateinit var streams: Streams

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "advance_video_stream")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

        flutterPluginBinding.platformViewRegistry.registerViewFactory("ExoPlayer", NativeViewFactory())

    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "createPlayer") {
            Log.d(TAG, "onMethodCall: createPlayer")
            createExoPlayer()
        } else if (call.method == "setVideoData") {
            Log.d(TAG, "onMethodCall: setVideoData")
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = null
        channel.setMethodCallHandler(null)
    }

    private fun createExoPlayer() {
        exoPlayer = ExoPlayer.Builder(context!!).build()
        Log.d(TAG, "createExoPlayer: exoPlayer $exoPlayer")


        CoroutineScope(Dispatchers.IO).launch {
            val streams: Streams? = VideoDataVM().getData("3UJ_mERvw3A")
            if (streams != null) {
                this@AdvanceVideoStreamPlugin.streams = streams
                updatePlayerItem()
            } else {
                Log.d(TAG, "createExoPlayer: CoroutineScope VideoDataVM().getData is null")
            }
        }

    }

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
}
