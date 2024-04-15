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
import com.example.advance_video_stream.view_model.VideoDataVM

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

    val nativeViewFactory = NativeViewFactory()

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "advance_video_stream")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

        flutterPluginBinding.platformViewRegistry.registerViewFactory("ExoPlayer", nativeViewFactory)

    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "setVideoData") {
            Log.d(TAG, "onMethodCall: setVideoData")

            val videoData = call.arguments as Map<*, *>

            val videoId: String = videoData["videoId"] as String
            val useHLS: Boolean = videoData["useHLS"] as Boolean

            Log.d(TAG, "onMethodCall: setVideoData videoId ${videoData["videoId"]}")
            Log.d(TAG, "onMethodCall: setVideoData useHLS ${videoData["useHLS"]}")

            updatePlayerItem(videoId, useHLS)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//        context = null

//        nativeViewFactory

        channel.setMethodCallHandler(null)
    }

    private fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        nativeViewFactory.updatePlayerItem(videoId, useHLS)
    }
}
