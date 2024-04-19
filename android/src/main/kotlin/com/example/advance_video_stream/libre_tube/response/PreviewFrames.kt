package com.example.advance_video_stream.libre_tube.response

import androidx.annotation.Keep

@Keep
data class PreviewFrames(
    val urls: List<String>,
    val frameWidth: Int,
    val frameHeight: Int,
    val totalCount: Int,
    val durationPerFrame: Long,
    val framesPerPageX: Int,
    val framesPerPageY: Int
)
