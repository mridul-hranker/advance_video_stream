package com.example.advance_video_stream.new_pipe_extractor


import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.StreamingService
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.time.Duration


object NewPipeExtractorHelper {
    private const val TAG = "NewPipeExtractorHelper"

    private var newPipeService: StreamingService

    private var retryCount: Int = 0
    private const val MAX_RETRY_COUNT: Int = 5

    init {
        val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClient.connectTimeout(Duration.ofMinutes(1))
        okHttpClient.callTimeout(Duration.ofMinutes(3))

        val serviceId = ServiceList.YouTube.serviceId
        NewPipe.init(DownloaderImpl(okHttpClient.build()))
        newPipeService = NewPipe.getService(serviceId)
    }

    fun getStreamingService(): StreamingService {
        return newPipeService
    }

    //        return StreamInfo.getInfo("https://www.youtube.com/watch?v=3jPFr94OxXY")
    fun getStreamInfo(videoId: String): StreamInfo? {
        retryCount = 0
        return streamInfoGetterWithRetry(videoId)
    }

    private fun streamInfoGetterWithRetry(videoId: String): StreamInfo? {
        try {
            return StreamInfo.getInfo("https://www.youtube.com/watch?v=$videoId")
        } catch (ex: Exception) {
            Log.e(TAG, "streamInfoGetterWithRetry: Exception ex message ${ex.message}", ex)
            if (retryCount < MAX_RETRY_COUNT) {
                retryCount += 1
                runBlocking { delay(3000) }
                Log.e(TAG, "streamInfoGetterWithRetry: retryCount $retryCount")
                return streamInfoGetterWithRetry(videoId)
            } else {
                return null
            }
        }
    }

}