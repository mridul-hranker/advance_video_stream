import 'package:flutter/widgets.dart';

import 'advance_video_stream_method_channel.dart';
import 'advance_video_stream_platform_interface.dart';

class AdvanceVideoStream {
  //Surface Player
  /// Not to use this for now this is for
  SurfacePlayer getSurfacePlayer() {
    return AdvanceVideoStreamPlatform.instance.getSurfacePlayer();
  }

  Future<void> playSurfacePlayer() {
    return AdvanceVideoStreamPlatform.instance.playSurfacePlayer();
  }

  Future<void> pauseSurfacePlayer() {
    return AdvanceVideoStreamPlatform.instance.pauseSurfacePlayer();
  }

  Future<void> setSurfacePlayerVideoData({required String videoId, bool useHLS = false}) {
    return AdvanceVideoStreamPlatform.instance.setSurfacePlayerVideoData(videoId, useHLS);
  }

  //Standard Player
  Future<void> play() {
    return AdvanceVideoStreamPlatform.instance.play();
  }

  Future<void> pause() {
    return AdvanceVideoStreamPlatform.instance.pause();
  }

  Future<void> setVideoData({required String videoId, bool useHLS = false}) {
    return AdvanceVideoStreamPlatform.instance.setVideoData(videoId, useHLS);
  }

  Future<int?> getCurrentPosition() {
    return AdvanceVideoStreamPlatform.instance.getCurrentPosition();
  }

  Future<void> setCurrentPosition(int position) {
    return AdvanceVideoStreamPlatform.instance.setCurrentPosition(position);
  }

  Future<void> changeOrientation(bool isLandscape) async {
    return AdvanceVideoStreamPlatform.instance.changeOrientation(isLandscape);
  }

  AdvancePlayer player({required double aspectRatio, double? height}) {
    return AdvanceVideoStreamPlatform.instance.player(aspectRatio, height);
  }
}
