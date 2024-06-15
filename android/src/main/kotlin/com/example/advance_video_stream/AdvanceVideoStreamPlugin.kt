package com.example.advance_video_stream

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.NonNull
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.advance_video_stream.libre_tube.dash.DashHelper

import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.view.Controller
import com.example.advance_video_stream.view.NativeViewFactory
import com.example.advance_video_stream.view.ExoPlayerView
import com.example.advance_video_stream.view_model.VideoDataVM

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.view.TextureRegistry
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

    private lateinit var textureRegistry: TextureRegistry

    private val nativeViewFactory = NativeViewFactory()

    private var surfacePlayer: ExoPlayerView? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "advance_video_stream")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

        textureRegistry = flutterPluginBinding.getTextureRegistry()

        flutterPluginBinding.platformViewRegistry.registerViewFactory("ExoPlayer", nativeViewFactory)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (isEmulator) {
            Log.d(TAG, "onMethodCall: you are on Emulator")
        } else if (call.method == "getSurfacePlayer") {
            Log.d(TAG, "onMethodCall: getPlayer")

            val handle: TextureRegistry.SurfaceTextureEntry = textureRegistry.createSurfaceTexture()

//            Log.d(TAG, "onMethodCall: getPlayer handle.id ${handle?.id()}")

            surfacePlayer = ExoPlayerView(context!!, handle)

            result.success(handle!!.id())
        } else if (call.method == "disposeSurfacePlayer") {
            Log.d(TAG, "onMethodCall: disposeSurfacePlayer")

            surfacePlayer?.dispose()
            surfacePlayer = null

        } else if (call.method == "playSurfacePlayer") {
            surfacePlayer?.playPlayer()
        } else if (call.method == "pauseSurfacePlayer") {
            surfacePlayer?.pausePlayer()
        } else if (call.method == "setSurfacePlayerVideoData") {
            Log.d(TAG, "onMethodCall: setSurfacePlayerVideoData")

            val videoData = call.arguments as Map<*, *>

            val videoId: String = videoData["videoId"] as String
            val useHLS: Boolean = videoData["useHLS"] as Boolean

//            Log.d(TAG, "onMethodCall: setVideoData videoId ${videoData["videoId"]}")
//            Log.d(TAG, "onMethodCall: setVideoData useHLS ${videoData["useHLS"]}")

            surfacePlayer?.updatePlayerItem(videoId, useHLS)
        } else if (call.method == "setVideoData") {
            Log.d(TAG, "onMethodCall: setVideoData")

            val videoData = call.arguments as Map<*, *>

            val videoId: String = videoData["videoId"] as String
            val useHLS: Boolean = videoData["useHLS"] as Boolean

//            Log.d(TAG, "onMethodCall: setVideoData videoId ${videoData["videoId"]}")
//            Log.d(TAG, "onMethodCall: setVideoData useHLS ${videoData["useHLS"]}")

            updatePlayerItem(videoId, useHLS)
        } else if (call.method == "getCurrentPosition") {
            Log.d(TAG, "onMethodCall: getCurrentPosition")

            val position = nativeViewFactory.getPosition()

//            Log.d(TAG, "onMethodCall: getCurrentPosition position $position")

            result.success(position)
        } else if (call.method == "setCurrentPosition") {
            Log.d(TAG, "onMethodCall: setCurrentPosition")

            val positionData = call.arguments as Map<*, *>

            val position: Long = positionData["position"].toString().toLong()

//            Log.d(TAG, "onMethodCall: getCurrentPosition position $position")

            nativeViewFactory.setPosition(position)
        } else if (call.method == "changeOrientation") {
            Log.d(TAG, "onMethodCall: changeOrientation")

            val orientationData = call.arguments as Map<*, *>

            val isLandscape: Boolean = orientationData["isLandscape"] as Boolean

//            Log.d(TAG, "onMethodCall: changeOrientation orientation $isLandscape")

            nativeViewFactory.setOrientationAspectRatio(isLandscape)
        } else if (call.method == "play") {
            Log.d(TAG, "onMethodCall: play")

            nativeViewFactory.play()
        } else if (call.method == "pause") {
            Log.d(TAG, "onMethodCall: pause")

            nativeViewFactory.pause()
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//        context = null

//        nativeViewFactory

        Log.d(TAG, "onDetachedFromEngine: called")
        surfacePlayer?.dispose()

        channel.setMethodCallHandler(null)
    }

    private fun updatePlayerItem(videoId: String, useHLS: Boolean = false) {
        nativeViewFactory.updatePlayerItem(videoId, useHLS)
    }


    //Reference from the device-info plugin by flutter community
    /**
     * A simple emulator-detection based on the flutter tools detection logic and a couple of legacy
     * detection systems
     */
    private val isEmulator: Boolean
        get() = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
}
