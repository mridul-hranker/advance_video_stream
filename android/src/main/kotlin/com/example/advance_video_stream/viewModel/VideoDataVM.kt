package com.example.advance_video_stream.viewModel

import com.example.advance_video_stream.libre_tube.response.Streams
import com.example.advance_video_stream.repo.VideoDataRepo

class VideoDataVM(private val repository: VideoDataRepo = VideoDataRepo) {

    suspend fun getData(videoId: String): Streams? {
        return repository.getData(videoId)
    }
}