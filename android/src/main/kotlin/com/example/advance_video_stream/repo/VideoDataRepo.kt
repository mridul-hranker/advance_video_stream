package com.example.advance_video_stream.repo

import android.util.Log
import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.network.APIClient
import com.example.advance_video_stream.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

object VideoDataRepo {
    private const val TAG = "VideoDataRepo"

    private val apiClient: APIClient = RetrofitManager.getClient

    suspend fun getData(videoId: String): Streams? {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                val response: Response<Streams> = apiClient.getVideoData(videoId)
                Log.d(TAG, "getData: response $response")
//                Log.d(TAG, "getData: response body ${response.body()}")
                return@async response.body()!!
            } catch (exception: Exception) {
                Log.d(TAG, "getData: exception $exception")
                return@async null;
            }
        }.await()

    }
}