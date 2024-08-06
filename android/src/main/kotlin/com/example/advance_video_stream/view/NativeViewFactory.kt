package com.example.advance_video_stream.view

import android.content.Context
import android.view.Surface
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.view.TextureRegistry

class NativeViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    lateinit var nativeView: NativeView
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?

        nativeView = NativeView(context, viewId, creationParams)

        return nativeView
    }

    fun updatePlayerItem(videoId: String, useHLS: Boolean = false) = nativeView.updatePlayerItem(videoId, useHLS)

    fun pause() = nativeView.pause()

    fun play() = nativeView.play()

    fun getVideoLength(): Long = nativeView.getVideoLength()

    fun getPosition(): Long = nativeView.getPosition()

    fun setPosition(position: Long) = nativeView.setPosition(position)

    fun setOrientationAspectRatio(isLandscape: Boolean) = nativeView.setOrientationAspectRatio(isLandscape)
}
