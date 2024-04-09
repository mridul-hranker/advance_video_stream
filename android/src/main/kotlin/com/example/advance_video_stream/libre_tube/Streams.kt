package com.example.advance_video_stream.libre_tube

import ChapterSegment
import MetaInfo
import PipedStream
import PreviewFrames
import StreamItem
import java.time.Instant

data class Streams(
    val title: String,
    val description: String,
    val uploadTimestamp: Instant,
    val uploader: String,
    val uploaderUrl: String,
    val uploaderAvatar: String? = null,
    val thumbnailUrl: String,
    val category: String,
    val license: String = "YouTube licence",
    val visibility: String = "public",
    val tags: List<String> = emptyList(),
    val metaInfo: List<MetaInfo> = emptyList(),
    val hls: String? = null,
    val dash: String? = null,
    val lbryId: String? = null,
    val uploaderVerified: Boolean,
    val duration: Long,
    val views: Long = 0,
    val likes: Long = 0,
    val dislikes: Long = 0,
    val audioStreams: List<PipedStream> = emptyList(),
    val videoStreams: List<PipedStream> = emptyList(),
    var relatedStreams: List<StreamItem> = emptyList(),
//    val subtitles: List<Subtitle> = emptyList(),
    val livestream: Boolean = false,
    val proxyUrl: String? = null,
    val chapters: List<ChapterSegment> = emptyList(),
    val uploaderSubscriberCount: Long = 0,
    val previewFrames: List<PreviewFrames> = emptyList()
) {


    /*fun toStreamItem(videoId: String): StreamItem {
        return StreamItem(
            url = videoId,
            title = title,
            thumbnail = thumbnailUrl,
            uploaderName = uploader,
            uploaderUrl = uploaderUrl,
            uploaderAvatar = uploaderAvatar,
            uploadedDate = uploadTimestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                .toString(),
            uploaded = uploadTimestamp.toEpochMilliseconds(),
            duration = duration,
            views = views,
            uploaderVerified = uploaderVerified,
            shortDescription = description
        )
    }*/

    companion object {
        const val categoryMusic = "Music"
    }
}
