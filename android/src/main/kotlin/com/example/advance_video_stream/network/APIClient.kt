package com.example.advance_video_stream.network

import androidx.annotation.Keep
import com.example.advance_video_stream.libre_tube.response.Streams
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

@Keep
interface APIClient {

    @GET("/streams/{videoId}")
    suspend fun getVideoData(@Path("videoId") videoId: String): Response<Streams>

}