package com.example.advance_video_stream.view_model

import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.repo.VideoDataRepo
import kotlinx.coroutines.Deferred

class VideoDataVM(private val repository: VideoDataRepo = VideoDataRepo) {

    suspend fun getData(videoId: String): Deferred<Streams?> {
        return repository.getData(videoId)
    }
}