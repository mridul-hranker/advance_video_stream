package com.example.advance_video_stream.new_pipe_extractor


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeStreamExtractor
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.time.Duration


object NewPipeExtractorHelper {


    private var newPipeService: StreamingService

    init {
        val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClient.connectTimeout(Duration.ofMinutes(1))
        okHttpClient.callTimeout(Duration.ofMinutes(3))

        val serviceId = ServiceList.YouTube.serviceId
        NewPipe.init(DownloaderImpl(okHttpClient.build()))
        YoutubeStreamExtractor.forceFetchIosClient(true)
        newPipeService = NewPipe.getService(serviceId);
    }

    fun getStreamingService(): StreamingService {
        return newPipeService
    }

    //        return StreamInfo.getInfo("https://www.youtube.com/watch?v=3jPFr94OxXY")
    fun getStreamInfo(videoId: String): StreamInfo? {
        return try {
            StreamInfo.getInfo("https://www.youtube.com/watch?v=$videoId")
        } catch (ex: Exception) {
            null
        }
    }

}