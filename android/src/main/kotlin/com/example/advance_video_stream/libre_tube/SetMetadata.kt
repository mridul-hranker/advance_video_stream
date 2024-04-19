package com.example.advance_video_stream.libre_tube

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.Keep
import androidx.media3.common.MediaItem
import com.example.advance_video_stream.R
//import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import com.example.advance_video_stream.libre_tube.response.Streams

@Keep
fun MediaItem.Builder.setMetadata(streams: Streams) = apply {
/*val appIcon = BitmapFactory.decodeResource(
    Resources.getSystem(),
    R.drawable.launch_background
)
val extras = bdleOf(
    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON to appIcon,
    MediaMetadataCompat.METADATA_KEY_TITLE to streams.title,
    MediaMetadataCompat.METADATA_KEY_ARTIST to streams.uploader
)*/
    setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(streams.title)
            .setArtist(streams.uploader)
            .setArtworkUri(Uri.parse(streams.thumbnailUrl))
//            .setExtras(extras)
            .build()
    )
}
