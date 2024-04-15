import 'dart:ffi';

import 'package:flutter/widgets.dart';
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

  Future<void> setVideoData(String videoId, bool useHLS) {
    throw UnimplementedError('setVideoData(String videoId, bool useHLS) has not been implemented.');
  }

  AdvancePlayer player(double aspectRatio, double? height) {
    throw UnimplementedError('player() has not been implemented.');
  }
}
