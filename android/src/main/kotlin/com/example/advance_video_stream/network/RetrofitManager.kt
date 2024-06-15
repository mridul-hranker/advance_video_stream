package com.example.advance_video_stream.network

import androidx.annotation.Keep
import com.google.net.cronet.okhttptransport.CronetCallFactory
import com.example.advance_video_stream.AdvanceVideoStreamPlugin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.chromium.net.CronetEngine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Keep
object RetrofitManager {

    val getClient: APIClient by lazy {
        Retrofit.Builder().baseUrl("https://pipedapi.kavin.rocks")
            .client(httpClient())
            .callFactory(CronetHelper.callFactory)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(APIClient::class.java)
    }


    private fun httpClient(): OkHttpClient {
        val httpClient: OkHttpClient.Builder = OkHttpClient().newBuilder()

        httpClient.addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )

        return httpClient.build()
    }

}


object CronetHelper {
    val cronetEngine: CronetEngine = CronetEngine.Builder(AdvanceVideoStreamPlugin.context)
        .enableHttp2(true)
        .enableQuic(true)
        .enableBrotli(true)
        .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 1024L * 1024L) // 1MiB
        .build()

    /*fun getCallFactory(): CronetCallFactory {
        return C
    }*/


    val callFactory: CronetCallFactory = CronetCallFactory.newBuilder(cronetEngine).build()
}