import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

import 'advance_video_stream_platform_interface.dart';

/// An implementation of [AdvanceVideoStreamPlatform] that uses method channels.
class MethodChannelAdvanceVideoStream extends AdvanceVideoStreamPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('advance_video_stream');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> setVideoData() async {
    final version = await methodChannel.invokeMethod<String>('setVideoData');
    return version;
  }

  @override
  Future<String?> createPlayer() async {
    final version = await methodChannel.invokeMethod<String>('createPlayer');
    return version;
  }

  @override
  PlatformViewLink player() {
    return PlatformViewLink(
      viewType: 'ExoPlayer',
      surfaceFactory: (context, controller) => AndroidViewSurface(
        controller: controller as AndroidViewController,
        hitTestBehavior: PlatformViewHitTestBehavior.opaque,
        gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
      ),
      onCreatePlatformView: (PlatformViewCreationParams params) {
        return PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: params.viewType,
          layoutDirection: TextDirection.ltr,
          onFocus: () => params.onFocusChanged(true),
        )
          ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
          ..create();
      },
    );
  }
}
