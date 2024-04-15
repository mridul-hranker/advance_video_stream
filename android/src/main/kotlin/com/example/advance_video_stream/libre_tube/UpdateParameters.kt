package com.example.advance_video_stream.libre_tube

import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
inline fun DefaultTrackSelector.updateParameters(
    actions: DefaultTrackSelector.Parameters.Builder.() -> Unit
) = setParameters(buildUponParameters().apply(actions))
