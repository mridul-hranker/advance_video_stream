package com.example.advance_video_stream.view

import android.content.Context
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    lateinit var nativeView: NativeView
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?

        nativeView = NativeView(context, viewId, creationParams)

        return nativeView
    }

    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) = nativeView.updatePlayerItem(videoId, useHLS)
}
