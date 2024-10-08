import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'advance_video_stream_method_channel.dart';

abstract class AdvanceVideoStreamPlatform extends PlatformInterface {
  /// Constructs a AdvanceVideoStreamPlatform.
  AdvanceVideoStreamPlatform() : super(token: _token);

  static final Object _token = Object();

  static AdvanceVideoStreamPlatform _instance = MethodChannelAdvanceVideoStream();

  /// The default instance of [AdvanceVideoStreamPlatform] to use.
  ///
  /// Defaults to [MethodChannelAdvanceVideoStream].
  static AdvanceVideoStreamPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AdvanceVideoStreamPlatform] when
  /// they register themselves.
  static set instance(AdvanceVideoStreamPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  //Surface Player
  SurfacePlayer getSurfacePlayer(double aspectRatio) {
    throw UnimplementedError('getSurfacePlayer has not been implemented.');
  }

  Future<void> disposeSurfacePlayer() async {
    throw UnimplementedError('disposeSurfacePlayer has not been implemented.');
  }

  Future<void> playSurfacePlayer() {
    throw UnimplementedError('playSurfacePlayer has not been implemented.');
  }

  Future<void> pauseSurfacePlayer() {
    throw UnimplementedError('pauseSurfacePlayer has not been implemented.');
  }

  Future<void> setSurfacePlayerVideoData(String videoId, bool useHLS) {
    throw UnimplementedError('setSurfacePlayerVideoData(String videoId, bool useHLS) has not been implemented.');
  }

  //Standard Player
  void play() {
    throw UnimplementedError('play has not been implemented.');
  }

  void pause() {
    throw UnimplementedError('pause has not been implemented.');
  }

  Future<int?> getCurrentPosition() {
    throw UnimplementedError('getCurrentPosition has not been implemented.');
  }

  Future<int?> getVideoLength() {
    throw UnimplementedError('getVideoLength has not been implemented.');
  }

  void setVideoData(String videoId, bool useHLS) {
    throw UnimplementedError('setVideoData(String videoId, bool useHLS) has not been implemented.');
  }

  void setCurrentPosition(int position) {
    throw UnimplementedError('setCurrentPosition has not been implemented.');
  }

  void changeOrientation(bool isLandscape) {
    throw UnimplementedError('changeOrientation has not been implemented.');
  }

  AdvancePlayer player(double aspectRatio, double? height) {
    throw UnimplementedError('player() has not been implemented.');
  }
}
