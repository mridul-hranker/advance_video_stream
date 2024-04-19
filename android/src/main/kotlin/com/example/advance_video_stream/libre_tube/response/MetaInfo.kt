package com.example.advance_video_stream.libre_tube.response

import androidx.annotation.Keep

@Keep
data class MetaInfo(
    val title: String,
    val description: String,
    val urls: List<String>,
    val urlTexts: List<String>
)
