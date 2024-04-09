import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

import 'advance_video_stream_platform_interface.dart';

class AdvanceVideoStream {
  Future<String?> getPlatformVersion() {
    return AdvanceVideoStreamPlatform.instance.getPlatformVersion();
  }

  Future<String?> setVideoData() {
    return AdvanceVideoStreamPlatform.instance.setVideoData();
  }

  Future<String?> createPlayer() {
    return AdvanceVideoStreamPlatform.instance.createPlayer();
  }

  PlatformViewLink player() {
    return AdvanceVideoStreamPlatform.instance.player();
  }
}
