package com.example.advance_video_stream.libre_tube

import androidx.annotation.Keep
import kotlin.math.pow
import kotlin.math.roundToInt

@Keep
fun Float.round(decimalPlaces: Int): Float {
    return (this * 10.0.pow(decimalPlaces.toDouble())).roundToInt() / 10.0.pow(
        decimalPlaces.toDouble()
    )
        .toFloat()
}
