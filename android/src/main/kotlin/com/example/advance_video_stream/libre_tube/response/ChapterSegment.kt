package com.example.advance_video_stream.libre_tube.response

import android.graphics.drawable.Drawable

data class ChapterSegment(
    val title: String,
    val image: String = "",
    val start: Long,
    // Used only for video highlights
    @Transient var highlightDrawable: Drawable? = null
) {
    companion object {
        /**
         * Length to show for a highlight in seconds
         */
        const val HIGHLIGHT_LENGTH = 10L
    }
}
