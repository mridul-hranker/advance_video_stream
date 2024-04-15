import 'advance_video_stream_method_channel.dart';
import 'advance_video_stream_platform_interface.dart';

class AdvanceVideoStream {
  Future<void> setVideoData({required String videoId, bool useHLS = false}) {
    return AdvanceVideoStreamPlatform.instance.setVideoData(videoId, useHLS);
  }

  AdvancePlayer player({required double aspectRatio, double? height}) {
    return AdvanceVideoStreamPlatform.instance.player(aspectRatio, height);
  }
}
